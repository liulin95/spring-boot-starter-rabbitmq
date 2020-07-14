package github.liulin.spring.rocketmq.annotation;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author liulin
 * @version $Id: RocketMqListenerAnnotationBeanPostProcessor.java, v0.1 2020/7/14 15:46 liulin Exp $$
 */
public class RocketMqListenerAnnotationBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
//        findListenerAnnotations(targetClass);
        Map<Method, Set<RocketMqListener>> annotatedMethod = MethodIntrospector.selectMethods(targetClass, (MethodIntrospector.MetadataLookup) method -> {
            Set<RocketMqListener> methods = findListenerAnnotations(method);
            return methods.size() > 0 ? methods : null;
        });
        return bean;
    }

    private Set<RocketMqListener> findListenerAnnotations(Class<?> targetClass) {
        Set<RocketMqListener> listenerSet = new HashSet<>();
        RocketMqListener listener = AnnotationUtils.findAnnotation(targetClass, RocketMqListener.class);
        if (listener != null) {
            listenerSet.add(listener);
        }
        RocketMqListeners listeners = AnnotationUtils.findAnnotation(targetClass, RocketMqListeners.class);
        if (listeners != null) {
            listenerSet.addAll(Arrays.asList(listeners.value()));
        }
        return listenerSet;
    }

    private Set<RocketMqListener> findListenerAnnotations(Method method) {
        Set<RocketMqListener> listenerSet = new HashSet<>();
        RocketMqListener listener = AnnotationUtils.findAnnotation(method, RocketMqListener.class);
        if (listener != null) {
            listenerSet.add(listener);
        }
        RocketMqListeners listeners = AnnotationUtils.findAnnotation(method, RocketMqListeners.class);
        if (listeners != null) {
            listenerSet.addAll(Arrays.asList(listeners.value()));
        }
        return listenerSet;
    }
}
