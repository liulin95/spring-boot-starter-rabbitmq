package github.liulin.spring.rocketmq.core;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * MQ Producer 内置实现
 *
 * @author liulin
 * @version $Id: RocketTemplate.java, v0.1 2020/7/14 14:28 liulin Exp $$
 */
public class RocketTemplate implements RocketProducer {
    public static final Logger logger = LoggerFactory.getLogger(RocketTemplate.class);
    private DefaultMQProducer mqProducer;

    /**
     * @param namesrvAddr   namesrvAddr
     * @param producerGroup 消费者组
     */
    public RocketTemplate(String namesrvAddr, String producerGroup) {
        mqProducer = new DefaultMQProducer();
        mqProducer.setNamesrvAddr(namesrvAddr);
        mqProducer.setProducerGroup(producerGroup);
    }

    /**
     * 消费者实例
     *
     * @param mqProducer
     */
    public RocketTemplate(DefaultMQProducer mqProducer) {
        this.mqProducer = mqProducer;
    }

    public void init() throws MQClientException {
        mqProducer.start();
        logger.debug("RocketMq producer {} has started.", mqProducer.getProducerGroup() + ":" + mqProducer.getInstanceName());
    }

    public void destroy() {
        mqProducer.shutdown();
        logger.debug("RocketMq producer {} shutdown now.", mqProducer.getProducerGroup() + ":" + mqProducer.getInstanceName());
    }

    @Override
    public boolean send(String topic, String body) {
        Message message = new Message();
        message.setTopic(topic);
        byte[] bytes;
        if (body != null) {
            try {
                bytes = body.getBytes(DEFAULT_ENCODING);
                message.setBody(bytes);
                SendResult result = send(message);
                return result.getSendStatus().equals(SendStatus.SEND_OK);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return false;
    }

    @Override
    public boolean sendBatch(String topic, List<String> msgs) {
        List<Message> messageList = msgs.stream().map(msg -> {
            Message message = new Message();
            message.setTopic(topic);
            byte[] bytes;
            if (msg != null) {
                try {
                    bytes = msg.getBytes(DEFAULT_ENCODING);
                    message.setBody(bytes);
                    return message;
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        try {
            return mqProducer.send(messageList).getSendStatus().equals(SendStatus.SEND_OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public SendResult send(Message message) {
        try {
            return mqProducer.send(message);
        } catch (Exception e) {
            logger.error("RocketMq send message error:", e);
        }
        return null;
    }

    public SendResult send(List<Message> messages) {
        try {
            return mqProducer.send(messages);
        } catch (Exception e) {
            logger.error("RocketMq send message error:", e);
        }
        return null;
    }
}
