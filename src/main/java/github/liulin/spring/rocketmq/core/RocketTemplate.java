package github.liulin.spring.rocketmq.core;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * @author liulin
 * @version $Id: RocketTemplate.java, v0.1 2020/7/14 14:28 liulin Exp $$
 */
public class RocketTemplate implements RocketProducer {
    public static final Logger logger = LoggerFactory.getLogger(RocketTemplate.class);
    private DefaultMQProducer mqProducer;

    public RocketTemplate(String namesrvAddr, String producerGroup) {
        mqProducer = new DefaultMQProducer();
        mqProducer.setNamesrvAddr(namesrvAddr);
        mqProducer.setProducerGroup(producerGroup);
    }

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
        byte[] bytes = new byte[0];
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
    public SendResult send(Message message) {
        try {
            return mqProducer.send(message);
        } catch (Exception e) {
            logger.error("RocketMq send message error:", e);
        }
        return null;
    }
}
