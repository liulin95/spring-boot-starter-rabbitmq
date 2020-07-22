package github.liulin.spring.rocketmq.core;

import github.liulin.spring.rocketmq.annotation.RocketMqHandler;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.common.message.MessageExt;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author liulin
 * @version $Id: RocketMqPullConsumerHandler.java, v0.1 2020/7/22 17:24 liulin Exp $$
 */
public class RocketMqPullConsumerHandler implements InvocationHandler {
    private DefaultLitePullConsumer consumer;
    private Object targetImpl;

    public RocketMqPullConsumerHandler(DefaultLitePullConsumer consumer) {
        this.consumer = consumer;
    }

    public RocketMqPullConsumerHandler(DefaultLitePullConsumer consumer, Object targetImpl) {
        this.consumer = consumer;
        this.targetImpl = targetImpl;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        RocketMqHandler handler = method.getAnnotation(RocketMqHandler.class);
        if (handler != null) {
            long timeOut = handler.pollTimeoutMillis();
            List<MessageExt> res = consumer.poll(timeOut);
            return res;
        }
        if (targetImpl != null) {
            return method.invoke(targetImpl, args);
        }
        return null;
    }
}
