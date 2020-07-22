package github.liulin.spring.rocketmq.annotation;

import java.lang.annotation.*;

/**
 * @author liulin
 * @version $Id: MqPushHandler.java, v0.1 2020/7/22 11:06 liulin Exp $$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface RocketMqHandler {
    long pollTimeoutMillis() default 1000 * 5;
}
