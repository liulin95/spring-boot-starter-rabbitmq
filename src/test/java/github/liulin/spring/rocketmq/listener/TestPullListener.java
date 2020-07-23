package github.liulin.spring.rocketmq.listener;

import github.liulin.spring.rocketmq.annotation.RocketMqPullConsumer;
import github.liulin.spring.rocketmq.core.PullConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.List;

@RocketMqPullConsumer(consumerGroup = "test01", topic = "TopicTest")
public class TestPullListener implements PullConsumer {

    public void handle() {
        List list = poll();
        System.out.println(list);
    }

}
