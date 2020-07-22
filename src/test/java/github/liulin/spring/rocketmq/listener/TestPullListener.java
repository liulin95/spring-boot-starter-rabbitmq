package github.liulin.spring.rocketmq.listener;

import github.liulin.spring.rocketmq.annotation.RocketMqPullConsumer;
import github.liulin.spring.rocketmq.core.PullConsumer;

@RocketMqPullConsumer(consumerGroup = "test01", topic = "TopicTest")
public class TestPullListener implements PullConsumer {
}
