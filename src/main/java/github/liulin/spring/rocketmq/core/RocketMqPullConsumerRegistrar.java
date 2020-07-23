package github.liulin.spring.rocketmq.core;

import github.liulin.spring.rocketmq.annotation.RocketMqPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.Map;
import java.util.Set;

/**
 * RocketMqPullConsumer注解扫描并创建代理注册到spring仓库
 *
 * @author liulin
 * @version $Id: RocketMqPullConsumerRegistrar.java, v0.1 2020/7/22 16:04 liulin Exp $$
 */
public class RocketMqPullConsumerRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware {
    private Environment environment;
    private ResourceLoader resourceLoader;


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取到扫描器
        ClassPathScanningCandidateComponentProvider scanningProvider = getScanner();
        scanningProvider.setResourceLoader(this.resourceLoader);
        // only filter the RocketMqPullConsumer
        // 过滤对象
        scanningProvider.addIncludeFilter(new AnnotationTypeFilter(RocketMqPullConsumer.class));

        // 扫描路径
        String basePackage = ClassUtils.getPackageName(importingClassMetadata.getClassName());

        Set<BeanDefinition> definitions = scanningProvider.findCandidateComponents(basePackage);
        for (BeanDefinition definition : definitions) {
            //再次判断，防止强转出错
            if (definition instanceof AnnotatedBeanDefinition) {
                registerToRegistry(registry, (AnnotatedBeanDefinition) definition);
            }
        }

    }

    /**
     * 注册到spring仓库
     *
     * @param registry
     * @param definition
     */
    private void registerToRegistry(BeanDefinitionRegistry registry, AnnotatedBeanDefinition definition) {
        AnnotationMetadata metadata = definition.getMetadata();
        // 注解里的属性值
        Map<String, Object> attributes = definition.getMetadata().getAnnotationAttributes(RocketMqPullConsumer.class.getName());

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RocketMqPullConsumerFactoryBean.class);
        // 根据注解配置创建consumer
        DefaultLitePullConsumer consumer = getConsumer(attributes);

        //设置factoryBean的属性值
        builder.addPropertyValue("type", metadata.getClassName());
        builder.addPropertyValue("consumer", consumer);
        builder.setDestroyMethodName("destroy");

        BeanDefinition proxyBeanDefinition = builder.getBeanDefinition();
        // 设置别名
        String[] name = (String[]) attributes.get("name");
        //没有设置别名，默认添加一个别名类名首字母小写
        if (name.length == 0) {
            String classFullName = metadata.getClassName();
            int lastPointIdx = classFullName.lastIndexOf(".");
            String className = classFullName.substring(lastPointIdx + 1, lastPointIdx + 2).toLowerCase() + classFullName.substring(lastPointIdx + 2);
            name = new String[]{className};
        }

        BeanDefinitionHolder holder = new BeanDefinitionHolder(proxyBeanDefinition, metadata.getClassName(), name);
        //注册到spring
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    private DefaultLitePullConsumer getConsumer(Map<String, Object> attributes) {
        DefaultLitePullConsumer consumer = new DefaultLitePullConsumer();
        String namesrvAddr = attributes.get("namesrvAddr").equals("") ? environment.getProperty("rocketmq.namesrvAddr") : attributes.get("namesrvAddr").toString();
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setAutoCommit((Boolean) attributes.get("autoCommit"));
        consumer.setInstanceName((String) attributes.get("instanceName"));
        consumer.setConsumerGroup((String) attributes.get("consumerGroup"));
        try {
            consumer.subscribe((String) attributes.get("topic"), (String) attributes.get("subExpression"));
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        consumer.setPullBatchSize((Integer) attributes.get("pullBatchSize"));
        consumer.setMessageModel((MessageModel) attributes.get("messageModel"));
        return consumer;
    }

    private ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                // Independent means it's top class not inner class
                // also not annotation
                // Independent指是顶级类，非内部类
                // 并且不是注解类
                return beanDefinition.getMetadata().isIndependent() && !beanDefinition.getMetadata().isAnnotation();
            }
        };
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
