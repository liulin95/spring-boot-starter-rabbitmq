package github.liulin.spring.rocketmq.annotation;

/**
 * @author liulin
 * @version $Id: MqPushHandler.java, v0.1 2020/7/22 11:06 liulin Exp $$
 */
public @interface RocketMqHandler {
    long pollTimeoutMillis() default  1000 * 5;
}
