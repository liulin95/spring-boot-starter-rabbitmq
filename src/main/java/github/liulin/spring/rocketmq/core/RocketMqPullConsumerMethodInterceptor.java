package github.liulin.spring.rocketmq.core;

import github.liulin.spring.rocketmq.annotation.RocketMqHandler;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author liulin
 * @version $Id: RocketMqPullConsumerMethodIntec.java, v0.1 2020/7/23 11:25 liulin Exp $$
 */
public class RocketMqPullConsumerMethodInterceptor implements MethodInterceptor {
    private DefaultLitePullConsumer consumer;

    public RocketMqPullConsumerMethodInterceptor(DefaultLitePullConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if ("getConsumer".equals(method.getName())) {
            return consumer;
        }
        RocketMqHandler handler = method.getAnnotation(RocketMqHandler.class);
        if (handler == null) {
            //父类调用,如果method中有内部调用会再次进入intercept
            return methodProxy.invokeSuper(o, objects);
        }
        long timeOut = handler.pollTimeoutMillis();
        return consumer.poll(timeOut);
    }
}
