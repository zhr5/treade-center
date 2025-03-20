package aop;

import org.springframework.web.ErrorResponse;

public class IdempotentException extends RuntimeException {
    private final boolean canRetry;

    public IdempotentException(String message, boolean canRetry) {
        super(message);
        this.canRetry = canRetry;
    }

    // 异常转换方法
    public ErrorResponse toErrorResponse() {
        return new ErrorResponse(canRetry ? "IDEMPOTENT_RETRY" : "IDEMPOTENT_LOCKED", getMessage());
    }
}

