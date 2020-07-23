package github.liulin.spring.rocketmq.core;

import github.liulin.spring.rocketmq.annotation.RocketMqPushConsumer;
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
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * push consumer 仓库
 *
 * @author liulin
 * @version $Id: RocketMqListenerRegistry.java, v0.1 2020/7/15 10:49 liulin Exp $$
 */
public class RocketMqPushConsumerRegistry implements SmartInitializingSingleton, EnvironmentAware, ApplicationContextAware {
    private final Logger logger = LoggerFactory.getLogger(RocketMqPushConsumerRegistry.class);
    private Environment environment;
    private ApplicationContext applicationContext;
    private Map<Method, List<DefaultMQPushConsumer>> listeners = new HashMap<>();
    private ReentrantLock putLock = new ReentrantLock();

    /**
     * 添加到仓库
     *
     * @param method
     * @param mqListenerSet
     */
    public void register(Method method, Set<RocketMqPushConsumer> mqListenerSet) {
        if (!listeners.containsKey(method)) {
            putLock.lock();
            try {
                if (!listeners.containsKey(method)) {
                    for (RocketMqPushConsumer listener : mqListenerSet) {
                        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
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
                                        logger.error("RocketMq push consumer [{}:{}] error:", consumer.getConsumerGroup(), consumer.getInstanceName(), e);
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
                                        logger.error("RocketMq push consumer [{}:{}] error:", consumer.getConsumerGroup(), consumer.getInstanceName(), e);
                                        return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                                    }
                                }
                            });
                        }
                        listeners.compute(method, (k, v) -> {
                            if (null == v) {
                                v = new ArrayList<>();
                            }
                            v.add(consumer);
                            return v;
                        });
                    }
                }
            } finally {
                putLock.unlock();
            }

        }
    }

    /**
     * 将注解中的属性设置到consumer
     *
     * @param consumer
     * @param mqListener
     */
    private void setProperties(DefaultMQPushConsumer consumer, RocketMqPushConsumer mqListener) {
        consumer.setConsumerGroup(mqListener.consumerGroup());
        try {
            consumer.subscribe(mqListener.topic(), mqListener.subExpression());
        } catch (MQClientException e) {
            logger.error("RocketMq push consumer [{}:{}] subscribe value is illegal", consumer.getConsumerGroup(), consumer.getInstanceName(), e);
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
    //spring bean 加载完后，启动所有的consumer
    public void afterSingletonsInstantiated() {
        for (Map.Entry<Method, List<DefaultMQPushConsumer>> entry : listeners.entrySet()) {
            List<DefaultMQPushConsumer> consumerList = entry.getValue();
            for (DefaultMQPushConsumer consumer : consumerList) {
                try {
                    consumer.start();
                    logger.debug("The push consumer [{}] defined in class [{}]  has started successful", consumer.getConsumerGroup() + ":" + consumer.getInstanceName(), entry.getKey().getDeclaringClass().getName());
                } catch (MQClientException e) {
                    logger.error("The push consumer [{}] defined in class [{}] can't start:", consumer.getConsumerGroup() + ":" + consumer.getInstanceName(), entry.getKey().getDeclaringClass().getName(), e);
                }
            }
        }
    }

    // 关闭所有的consumer
    public void destroy() {
        for (Map.Entry<Method, List<DefaultMQPushConsumer>> entry : listeners.entrySet()) {
            List<DefaultMQPushConsumer> consumerList = entry.getValue();
            for (DefaultMQPushConsumer consumer : consumerList) {
                consumer.shutdown();
                logger.debug("The push consumer [{}] defined in class [{}]  shutdown now", consumer.getConsumerGroup() + ":" + consumer.getInstanceName(), entry.getKey().getDeclaringClass().getName());
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
