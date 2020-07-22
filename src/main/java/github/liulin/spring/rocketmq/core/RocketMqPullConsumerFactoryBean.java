package github.liulin.spring.rocketmq.core;

import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Enhancer;

/**
 * @author liulin
 * @version $Id: RocketMqPullConsumerFactoryBean.java, v0.1 2020/7/22 17:10 liulin Exp $$
 */
public class RocketMqPullConsumerFactoryBean implements FactoryBean {
    @Override
    public Object getObject() throws Exception {
//        Enhancer enhancer = Enhancer.
        DefaultLitePullConsumer pullConsumer = new DefaultLitePullConsumer();

        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }
}
