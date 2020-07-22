package github.liulin.spring.rocketmq.listener;

import github.liulin.spring.rocketmq.annotation.RocketMqPullConsumer;
import github.liulin.spring.rocketmq.core.PullConsumer;

@RocketMqPullConsumer(consumerGroup = "test02", topic = "TopicTest", name = "test2PullListener")
public class Test2PullListener implements PullConsumer {
}
