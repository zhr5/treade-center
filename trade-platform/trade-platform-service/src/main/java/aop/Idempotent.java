package aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    /**
     * 幂等键过期时间（秒）
     */
    int expire() default 300;

    /**
     * 业务类型（用于社区多场景隔离）
     */
    String bizType() default "";
}

