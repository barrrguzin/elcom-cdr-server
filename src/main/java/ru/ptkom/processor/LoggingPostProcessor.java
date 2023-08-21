package ru.ptkom.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class LoggingPostProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(LoggingPostProcessor.class);

    private final ApplicationContext applicationContext;

    public LoggingPostProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        log.info("Created bean: " + beanName);
        if (beanName.contains("Chain")) {
            return bean;
        } else {
            return bean;
        }
    }
}
