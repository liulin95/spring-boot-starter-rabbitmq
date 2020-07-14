package github.liulin.spring.rocketmq.annotation;

import java.lang.annotation.*;

/**
 * @author liulin
 * @version $Id: RocketMqListeners.java, v0.1 2020/7/14 16:07 liulin Exp $$
 */
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RocketMqListeners {
    RocketMqListener[] value();
}
