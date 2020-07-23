package github.liulin.spring.rocketmq.core;

import github.liulin.spring.rocketmq.annotation.RocketMqHandler;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author liulin
 * @version $Id: RocketMqPullConsumerHandler.java, v0.1 2020/7/22 17:24 liulin Exp $$
 */
public class RocketMqPullConsumerInvocationHandler implements InvocationHandler {
    private DefaultLitePullConsumer consumer;
    private FactoryBean targetFactoryBean;

    public RocketMqPullConsumerInvocationHandler(DefaultLitePullConsumer consumer, FactoryBean targetFactoryBean) {
        this.consumer = consumer;
        this.targetFactoryBean = targetFactoryBean;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        RocketMqHandler handler = method.getAnnotation(RocketMqHandler.class);
        if (handler != null) {
            long timeOut = handler.pollTimeoutMillis();
            List<MessageExt> res = consumer.poll(timeOut);
            return res;
        }
        // object 方法
        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(targetFactoryBean, args);
        }
        if ("getConsumer".equals(method.getName())) {
            return consumer;
        }
        return null;
    }
}
