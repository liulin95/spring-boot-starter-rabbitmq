package github.liulin.spring.rocketmq.core;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.List;

/**
 * @author liulin
 * @version $Id: RocketProducer.java, v0.1 2020/7/14 14:27 liulin Exp $$
 */
public interface RocketProducer {
    String DEFAULT_ENCODING = "utf-8";

    boolean send(String topic, String body);

    boolean sendBatch(String topic, List<String> msgs);

    SendResult send(Message message);

    SendResult send(List<Message> messages);
}
