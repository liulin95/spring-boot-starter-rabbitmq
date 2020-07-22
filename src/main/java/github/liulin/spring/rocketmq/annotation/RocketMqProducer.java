package github.liulin.spring.rocketmq.annotation;

/**
 * @author liul
 * @version $Id: RocketMqConsumer.java, v0.1 2020/7/14 11:25 liul Exp $$
 */
public @interface RocketMqProducer {
    String namesrvAddr() default "";

    String producerGroup() default "";

}
