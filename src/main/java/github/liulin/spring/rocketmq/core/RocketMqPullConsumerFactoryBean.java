package github.liulin.spring.rocketmq.core;

import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.cglib.proxy.Enhancer;

import java.lang.reflect.Proxy;

/**
 * PullConsumer的代理bean
 *
 * @author liulin
 * @version $Id: RocketMqPullConsumerFactoryBean.java, v0.1 2020/7/22 17:10 liulin Exp $$
 */
public class RocketMqPullConsumerFactoryBean implements FactoryBean, SmartInitializingSingleton {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Class<? extends PullConsumer> type;

    private DefaultLitePullConsumer consumer;

    @Override
    public Object getObject() throws Exception {
        //创建代理 仅代理poll和获取consumer实例两个方法
        Object proxy;
        if (type.isInterface()) {
            //没有实现类，使用java动态代理
            proxy = Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new RocketMqPullConsumerInvocationHandler(consumer, this));
        } else {
            //存在实现类，使用CGLIB
            Enhancer enhancer = new Enhancer();
            enhancer.setCallback(new RocketMqPullConsumerMethodInterceptor(consumer));
            enhancer.setClassLoader(type.getClassLoader());
            enhancer.setSuperclass(type);
            enhancer.setInterfaces(type.getInterfaces());
            proxy = enhancer.create();
        }

        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        if (type == null) {
            return null;
        }
        return type;
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
    /**
     * 所有bean加载完毕后，启动consumer
     */
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

    /**
     * 销毁方法
     */
    public void destroy() {
        consumer.shutdown();
        logger.debug("The pull consumer [{}:{}] defined in class [{}] shutdown now.",
                consumer.getConsumerGroup(), consumer.getInstanceName(), type.getName());
    }
}
