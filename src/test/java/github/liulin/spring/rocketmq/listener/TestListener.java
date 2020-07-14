package github.liulin.spring.rocketmq.listener;

import github.liulin.spring.rocketmq.annotation.RocketMqListener;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestListener {

    @RocketMqListener(consumerGroup = "test01",topic = "TopicTest")
    public void handle(List<MessageExt> msg, ConsumeConcurrentlyContext context){
        System.out.println(msg);
    }
}
