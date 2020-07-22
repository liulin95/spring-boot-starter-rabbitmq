package github.liulin.spring.rocketmq.core;

import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.SmartInitializingSingleton;

import java.lang.reflect.Proxy;

/**
 * @author liulin
 * @version $Id: RocketMqPullConsumerFactoryBean.java, v0.1 2020/7/22 17:10 liulin Exp $$
 */
public class RocketMqPullConsumerFactoryBean implements FactoryBean, SmartInitializingSingleton {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Class<? extends PullConsumer> type;

    private DefaultLitePullConsumer consumer;

    @Override
    public Object getObject() throws Exception {
        Object proxy;
        if (type.isInterface()) {
            proxy = Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new RocketMqPullConsumerHandler(consumer));
        } else {
            Object target = type.newInstance();
            proxy = Proxy.newProxyInstance(type.getClassLoader(), type.getInterfaces(), new RocketMqPullConsumerHandler(consumer, target));
        }

        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        if (type == null) {
            return null;
        }
        return type;
//        if (type.isInterface()) {
//            return type;
//        }
//        return PullConsumer.class;
    }

    public void init() {


    }

    public Class<? extends PullConsumer> getType() {
        return type;
    }

    public void setType(Class<? extends PullConsumer> type) {
        this.type = type;
    }

    public DefaultLitePullConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(DefaultLitePullConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void afterSingletonsInstantiated() {
        try {
            consumer.start();
            logger.debug("The pull consumer [{}:{}] defined in class [{}]  has started successful",
                    consumer.getConsumerGroup(), consumer.getInstanceName(), type.getName());
        } catch (MQClientException e) {
            logger.debug("The pull consumer [{}:{}] defined in class [{}]   can't start:",
                    consumer.getConsumerGroup(), consumer.getInstanceName(), type.getName(), e);
        }
    }
}
