package aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class IdempotentAspect {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Pointcut("@annotation(com.xxx.Idempotent)")
    public void idempotentPointcut() {}

    @Around("idempotentPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 获取注解配置
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Idempotent idempotent = signature.getMethod().getAnnotation(Idempotent.class);

        // 2. 构建幂等键
        String idempotentKey = buildIdempotentKey(joinPoint, idempotent);

        // 3. 原子性设置幂等键
        Boolean success = redisTemplate.opsForValue().setIfAbsent(
                idempotentKey, "processing", idempotent.expire(), TimeUnit.SECONDS);

        if (Boolean.FALSE.equals(success)) {
            // 4. 已存在请求处理中
            String status = redisTemplate.opsForValue().get(idempotentKey);
            if ("completed".equals(status)) {
                throw new IdempotentException("请求已处理", true); // 幂等返回
            } else {
                throw new IdempotentException("请求正在处理中", false);
            }
        }

        try {
            // 5. 执行业务逻辑
            Object result = joinPoint.proceed();

            // 6. 标记请求完成
            redisTemplate.opsForValue().set(idempotentKey, "completed", 30, TimeUnit.SECONDS);
            return result;
        } catch (Exception e) {
            // 7. 异常时清理锁
            redisTemplate.delete(idempotentKey);
            throw e;
        }
    }

    private String buildIdempotentKey(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        // 示例：community:order:user123:request456
        String userId = getCurrentUserId();
        String requestId = ((BaseRequest) joinPoint.getArgs()[0]).getRequestId();
        return String.format("idempotent:%s:%s:%s",
                idempotent.bizType(), userId, requestId);
    }
}

/*    // 社区电商特殊配置（更严格的幂等控制）
    @Idempotent(expire = 600, bizType = "community_mall")
    public OrderResult createCommunityOrder(OrderCreateRequest request) {
        // ...
    }

    // 物业缴费配置（较短有效期）
    @Idempotent(expire = 120, bizType = "property_fee")
    public OrderResult createPropertyOrder(OrderCreateRequest request) {
        // ...
    }*/

/*// 应急订单跳过部分校验
public class EmergencyOrderStrategy implements IdempotentStrategy {

    @Override
    public boolean check(RequestContext context) {
        OrderCreateRequest request = (OrderCreateRequest) context.getRequest();
        return request.getExtParams().containsKey("emergency_flag");
    }

    @Override
    public String buildKey(RequestContext context) {
        // 应急订单使用社区+手机号作为幂等键
        return "emergency:" + request.getCommunityId() + ":" + request.getMobile();
    }
}*/


