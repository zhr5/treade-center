import aop.IdempotentException;
import org.junit.jupiter.api.Test;

public class testIdempotent {
    @Test
    public void testIdempotent() {
        // 第一次请求
        OrderResult result1 = orderService.createOrder(request);

        // 重复请求
        assertThrows(IdempotentException.class, () -> {
            orderService.createOrder(request);
        });

        // 修改请求ID后重试
        request.setRequestId("new_request_id");
        OrderResult result2 = orderService.createOrder(request);
        assertNotEquals(result1.getOrderId(), result2.getOrderId());
    }

}

/*该方案通过以下方式保障智慧社区场景安全：
        分层防护：请求签名 → 时间窗口校验 → 幂等控制
        场景化配置：不同业务类型（电商/缴费）采用不同策略
        应急通道：特殊社区场景（如台风应急）定制处理
        监控集成：通过监控系统及时发现问题*/
