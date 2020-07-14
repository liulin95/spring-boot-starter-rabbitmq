package github.liulin.spring.rocketmq.autoconfigure;

import github.liulin.spring.rocketmq.RocketProperties;
import github.liulin.spring.rocketmq.annotation.RocketMqListenerAnnotationBeanPostProcessor;
import github.liulin.spring.rocketmq.core.RocketTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liulin
 * @version $Id: RocketMqAutoConfigure.java, v0.1 2020/7/14 14:18 liulin Exp $$
 */
@Configuration
@EnableConfigurationProperties(value = RocketProperties.class)
public class RocketMqAutoConfigure {
    @Autowired
    private RocketProperties properties;

    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(RocketTemplate.class)
    public RocketTemplate rocketTemplate() {
        RocketTemplate rocketTemplate = new RocketTemplate(properties.getNamesrvAddr(), properties.getProducerGroup());
        return rocketTemplate;
    }

    @Bean
    public RocketMqListenerAnnotationBeanPostProcessor rocketMqListenerAnnotationBeanPostProcessor() {
        return new RocketMqListenerAnnotationBeanPostProcessor();
    }

}
