package github.liulin.spring.rocketmq.core;

import github.liulin.spring.rocketmq.annotation.RocketMqHandler;
import github.liulin.spring.rocketmq.annotation.RocketMqPushConsumer;
import github.liulin.spring.rocketmq.annotation.RocketMqPushConsumers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liulin
 * @version $Id: RocketMqListenerAnnotationBeanPostProcessor.java, v0.1 2020/7/14 15:46 liulin Exp $$
 */
public class RocketMqListenerAnnotationBeanPostProcessor implements BeanPostProcessor {
    private final Logger logger = LoggerFactory.getLogger(RocketMqListenerAnnotationBeanPostProcessor.class);
    private RocketMqPushConsumerRegistry listenerRegistry;

    private Set<Class<?>> noListenerClass = Collections.newSetFromMap(new ConcurrentHashMap<>(64));


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        if (!noListenerClass.contains(bean.getClass())) {
            Set<RocketMqPushConsumer> annotatedClass = findListenerAnnotations(targetClass); //注解在类上
            Map<Method, Set<RocketMqPushConsumer>> annotatedMethod = MethodIntrospector.selectMethods(targetClass, //注解在方法上
                    (MethodIntrospector.MetadataLookup) method -> {
                        Set<RocketMqPushConsumer> methods = findListenerAnnotations(method);
                        return methods.size() > 0 ? methods : null;
                    });
            if (!annotatedMethod.isEmpty()) {
                for (Map.Entry<Method, Set<RocketMqPushConsumer>> entry : annotatedMethod.entrySet()) {
                    listenerRegistry.register(entry.getKey(), entry.getValue());
                }
            } else {
                noListenerClass.add(bean.getClass());
            }
            if (!annotatedClass.isEmpty()) {
                // 查找消息处理方法
                Set<Method> handlerMethods = MethodIntrospector.selectMethods(targetClass, (ReflectionUtils.MethodFilter) method ->
                        AnnotationUtils.findAnnotation(method, RocketMqHandler.class) != null);
                if (!handlerMethods.isEmpty()) {
                    // 在类上的注解内部只取一个处理消息的方法
                    if (handlerMethods.size() > 1) {
                        logger.warn("Annotation 'RocketMqPushListener' work on class only support one handler, class : {}", bean.getClass());
                    }
                    Method method = handlerMethods.stream().findFirst().get();
                    listenerRegistry.register(method, annotatedClass);
                }
            }
        }
        return bean;
    }

    /**
     * 类级别查找带有注解的类
     *
     * @param targetClass
     * @return
     */
    private Set<RocketMqPushConsumer> findListenerAnnotations(Class<?> targetClass) {
        Set<RocketMqPushConsumer> listenerSet = new HashSet<>();
        RocketMqPushConsumer listener = AnnotationUtils.findAnnotation(targetClass, RocketMqPushConsumer.class);
        if (listener != null) {
            listenerSet.add(listener);
        }
        RocketMqPushConsumers listeners = AnnotationUtils.findAnnotation(targetClass, RocketMqPushConsumers.class);
        if (listeners != null) {
            listenerSet.addAll(Arrays.asList(listeners.value()));
        }
        return listenerSet;
    }

    /**
     * 查找带有注解的方法
     *
     * @param method
     * @return
     */
    private Set<RocketMqPushConsumer> findListenerAnnotations(Method method) {
        Set<RocketMqPushConsumer> listenerSet = new HashSet<>();
        RocketMqPushConsumer listener = AnnotationUtils.findAnnotation(method, RocketMqPushConsumer.class);
        if (listener != null) {
            listenerSet.add(listener);
        }
        RocketMqPushConsumers listeners = AnnotationUtils.findAnnotation(method, RocketMqPushConsumers.class);
        if (listeners != null) {
            listenerSet.addAll(Arrays.asList(listeners.value()));
        }
        return listenerSet;
    }

    public RocketMqPushConsumerRegistry getListenerRegistry() {
        return listenerRegistry;
    }

    public void setListenerRegistry(RocketMqPushConsumerRegistry listenerRegistry) {
        this.listenerRegistry = listenerRegistry;
    }
}
