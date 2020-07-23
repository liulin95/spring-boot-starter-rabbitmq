package github.liulin.spring.rocketmq.configure;

import github.liulin.spring.rocketmq.RocketProperties;
import github.liulin.spring.rocketmq.core.RocketMqListenerAnnotationBeanPostProcessor;
import github.liulin.spring.rocketmq.core.RocketMqPushConsumerRegistry;
import github.liulin.spring.rocketmq.core.RocketTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author liulin
 * @version $Id: RocketMqAutoConfigure.java, v0.1 2020/7/14 14:18 liulin Exp $$
 */
@EnableConfigurationProperties(value = RocketProperties.class)
public class RocketMqConfigure {
    @Autowired
    private RocketProperties properties;

    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(RocketTemplate.class)
    public RocketTemplate rocketTemplate() {
        RocketTemplate rocketTemplate = new RocketTemplate(properties.getNamesrvAddr(), properties.getProducerGroup());
        return rocketTemplate;
    }

    @Bean(destroyMethod = "destroy")
    public RocketMqPushConsumerRegistry rocketMqListenerRegistry() {
        return new RocketMqPushConsumerRegistry();
    }

    @Bean
    public RocketMqListenerAnnotationBeanPostProcessor rocketMqListenerAnnotationBeanPostProcessor(RocketMqPushConsumerRegistry rocketMqPushConsumerRegistry) {
        RocketMqListenerAnnotationBeanPostProcessor processor = new RocketMqListenerAnnotationBeanPostProcessor();
        processor.setListenerRegistry(rocketMqPushConsumerRegistry);
        return processor;
    }

}
