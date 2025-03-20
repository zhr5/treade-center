package filter;

import jakarta.security.auth.message.AuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Component
public class ReplayAttackFilter extends OncePerRequestFilter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 时间允许误差（5分钟）
    private static final long TIME_TOLERANCE = 300_000;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 获取必要请求头
        String nonce = request.getHeader("X-Nonce");
        String timestamp = request.getHeader("X-Timestamp");
        String signature = request.getHeader("X-Signature");

        // 2. 基础校验
        if (StringUtils.isEmpty(nonce) || StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(signature)) {
            throw new AuthException("INVALID_HEADER", "缺少安全头部参数");
        }

        // 3. 时间戳校验（防止重放攻击）
        long serverTime = System.currentTimeMillis();
        long clientTime = Long.parseLong(timestamp);
        if (Math.abs(serverTime - clientTime) > TIME_TOLERANCE) {
            throw new AuthException("TIMESTAMP_EXPIRED", "请求已过期");
        }

        // 4. Nonce校验（防止重复请求）
        String redisKey = "nonce:" + nonce;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            throw new AuthException("REPLAY_ATTACK", "检测到重复请求");
        }
        redisTemplate.opsForValue().set(redisKey, "1", 5, TimeUnit.MINUTES);

        // 5. 签名校验（示例：HMAC-SHA256）
        String appSecret = getAppSecret(request); // 根据请求获取对应社区密钥
        String generatedSign = generateSignature(request, appSecret, nonce, timestamp);
        if (!generatedSign.equals(signature)) {
            throw new AuthException("SIGNATURE_MISMATCH", "签名校验失败");
        }

        filterChain.doFilter(request, response);
    }

    private String generateSignature(HttpServletRequest request, String appSecret,
                                     String nonce, String timestamp) {
        try {
            String paramStr = getSortedParamString(request);
            String data = nonce + timestamp + paramStr;
            Mac sha256 = Mac.getInstance("HmacSHA256");
            sha256.init(new SecretKeySpec(appSecret.getBytes(), "HmacSHA256"));
            byte[] hash = sha256.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(hash);
        } catch (Exception e) {
            throw new AuthException("SIGN_FAILED", "签名生成异常");
        }
    }
}

