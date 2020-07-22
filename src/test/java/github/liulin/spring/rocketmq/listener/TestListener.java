package github.liulin.spring.rocketmq.listener;

import github.liulin.spring.rocketmq.annotation.RocketMqPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Component
public class TestListener {

    @RocketMqPushConsumer(consumerGroup = "test01",topic = "TopicTest")
    public void handle(List<MessageExt> msgs, ConsumeConcurrentlyContext context){
        for (MessageExt msg : msgs) {

            //消费者获取消息 这里只输出 不做后面逻辑处理
            String body = null;
            try {
                body = new String(msg.getBody(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            System.out.println(String.format("Consumer-获取消息-主题topic为=%s, 消费消息为=%s", msg.getTopic(), body));
        }
    }
}
