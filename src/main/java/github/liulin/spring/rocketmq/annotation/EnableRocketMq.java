package github.liulin.spring.rocketmq.annotation;

import github.liulin.spring.rocketmq.configure.RocketMqConfigure;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author liulin
 * @version $Id: EnableRocketMq.java, v0.1 2020/7/22 15:48 liulin Exp $$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(value = {RocketMqConfigure.class})
public @interface EnableRocketMq {
}
