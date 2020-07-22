package github.liulin.spring.rocketmq.annotation;

import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * @author liulin
 * @version $Id: RockerMqPullConsumer.java, v0.1 2020/7/22 11:47 liulin Exp $$
 */
public @interface RocketMqPullConsumer {
    String namesrvAddr() default "";

    String consumerGroup();

    String topic();

    String subExpression() default "*";

    String instanceName() default "";

    MessageModel messageModel() default MessageModel.CLUSTERING;

    int pullBatchSize() default 10;

    boolean autoCommit() default true;

}
