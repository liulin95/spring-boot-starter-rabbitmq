package github.liulin.spring.rocketmq.core;

import github.liulin.spring.rocketmq.annotation.RocketMqHandler;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author liulin
 * @version $Id: RocketMqPullConsumerHandler.java, v0.1 2020/7/22 17:24 liulin Exp $$
 */
public class RocketMqPullConsumerHandler implements MethodInterceptor {
    private DefaultLitePullConsumer consumer;

    public RocketMqPullConsumerHandler(DefaultLitePullConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        RocketMqHandler handler = method.getAnnotation(RocketMqHandler.class);
        long timeOut = handler.pollTimeoutMillis();
        if(handler!=null){
//            if(objects.length!=1||objects[0])
                List<MessageExt> res = consumer.poll(timeOut);
        }
        return null;
    }

}
