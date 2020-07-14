package github.liulin.spring.rocketmq.annotation;

import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.lang.annotation.*;

/**
 * @author liulin
 * @version $Id: RocketMqListener.java, v0.1 2020/7/14 11:31 liulin Exp $$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Repeatable(RocketMqListeners.class)
public @interface RocketMqListener {
    String type() default "";
    String namesrvAddr() default "";
    String consumerGroup();
    String topic();
    String subExpression() default "*";
    String instanceName() default "";
    MessageModel model() default MessageModel.CLUSTERING;
    int pullBatchSize() default -1;
}
