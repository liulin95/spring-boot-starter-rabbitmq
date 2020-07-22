package github.liulin.spring.rocketmq.core;

import github.liulin.spring.rocketmq.annotation.RocketMqPullConsumer;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
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
 * RocketMqPullConsumer scanner
 *
 * @author liulin
 * @version $Id: RocketMqPullConsumerRegistrar.java, v0.1 2020/7/22 16:04 liulin Exp $$
 */
public class RocketMqPullConsumerRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware {
    private Environment environment;
    private ResourceLoader resourceLoader;


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanningProvider = getScanner();
        scanningProvider.setResourceLoader(this.resourceLoader);
        // only filter the RocketMqPullConsumer
        scanningProvider.addIncludeFilter(new AnnotationTypeFilter(RocketMqPullConsumer.class));

        String basePackage = ClassUtils.getPackageName(importingClassMetadata.getClassName());

        Set<BeanDefinition> definitions = scanningProvider.findCandidateComponents(basePackage);
        for (BeanDefinition definition : definitions) {
            if (definition instanceof AnnotatedBeanDefinition) {
                registerToRegistry(registry, (AnnotatedBeanDefinition) definition);
            }
        }

    }

    private void registerToRegistry(BeanDefinitionRegistry registry, AnnotatedBeanDefinition definition) {
        AnnotationMetadata metadata = definition.getMetadata();
        Map attributes = definition.getMetadata().getAnnotationAttributes(RocketMqPullConsumer.class.getName());



        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RocketMqPullConsumerFactoryBean.class);

        BeanDefinition proxyBeanDefinition = builder.getBeanDefinition();


        BeanDefinitionHolder holder = new BeanDefinitionHolder(proxyBeanDefinition,metadata.getClassName());

    }

    private ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                // Independent means it's top class not inner class
                // also not annotation
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
