package github.liulin.spring.rocketmq.core;

import github.liulin.spring.rocketmq.annotation.RocketMqListener;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liulin
 * @version $Id: RocketMqListenerRegistry.java, v0.1 2020/7/15 10:49 liulin Exp $$
 */
public class RocketMqListenerRegistry implements SmartInitializingSingleton, EnvironmentAware, ApplicationContextAware {
    private final Logger logger = LoggerFactory.getLogger(RocketMqListenerRegistry.class);
    private Environment environment;
    private ApplicationContext applicationContext;
    private Map<Method, DefaultMQPushConsumer> listeners = new ConcurrentHashMap();

    public void add(Method method, Set<RocketMqListener> mqListenerSet) {
        if (!listeners.containsKey(method)) {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
            RocketMqListener listener = mqListenerSet.stream().findFirst().get();
            setProperties(consumer, listener);
            Object containerBean = applicationContext.getBean(method.getDeclaringClass());
            if (listener.listenerType().equals(MessageListenerConcurrently.class)) {
                consumer.registerMessageListener(new MessageListenerConcurrently() {
                    @Override
                    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                        try {
                            method.invoke(containerBean, msgs, context);
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        } catch (Exception e) {
                            logger.error("RocketMq consumer[{}:{}] error:", consumer.getConsumerGroup(), consumer.getInstanceName(), e);
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }
                    }
                });
            } else {
                consumer.registerMessageListener(new MessageListenerOrderly() {
                    @Override
                    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                        try {
                            method.invoke(containerBean, msgs, context);
                            return ConsumeOrderlyStatus.SUCCESS;
                        } catch (Exception e) {
                            logger.error("RocketMq consumer[{}:{}] error:", consumer.getConsumerGroup(), consumer.getInstanceName(), e);
                            return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                        }
                    }
                });
            }

            listeners.put(method, consumer);
        }
    }

    private void setProperties(DefaultMQPushConsumer consumer, RocketMqListener mqListener) {
        consumer.setConsumerGroup(mqListener.consumerGroup());
        try {
            consumer.subscribe(mqListener.topic(), mqListener.subExpression());
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        if (propertiesDefault(mqListener.namesrvAddr())) {
            consumer.setNamesrvAddr(environment.getProperty("rocketmq.namesrvAddr"));
        } else {
            consumer.setNamesrvAddr(mqListener.namesrvAddr());
        }
        if (!propertiesDefault(mqListener.instanceName())) {
            consumer.setInstanceName(mqListener.instanceName());
        }
        if (!propertiesDefault(mqListener.pullBatchSize())) {
            consumer.setPullBatchSize(mqListener.pullBatchSize());
        }
        consumer.setMessageModel(mqListener.messageModel());
    }

    @Override
    public void afterSingletonsInstantiated() {
        for (Map.Entry<Method, DefaultMQPushConsumer> entry : listeners.entrySet()) {
            DefaultMQPushConsumer consumer = entry.getValue();
            try {
                consumer.start();
                logger.debug("The listener[{}] defined in class[{}]  has started successful", consumer.getConsumerGroup() + ":" + consumer.getInstanceName(), entry.getKey().getDeclaringClass().getName());
            } catch (MQClientException e) {
                logger.error("The listener[{}] defined in class[{}] can't start:", consumer.getConsumerGroup() + ":" + consumer.getInstanceName(), entry.getKey().getDeclaringClass().getName(), e);
            }
        }
    }

    private boolean propertiesDefault(Object propertyValue) {
        if (propertyValue instanceof String) {
            return "".equals(propertyValue);
        }
        if (propertyValue instanceof Integer) {
            return Integer.valueOf(-1).equals(propertyValue);
        }

        return true;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
