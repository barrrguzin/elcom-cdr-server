package ru.ptkom.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;


import javax.servlet.Filter;

@Configuration
public class SpringWebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[] {};
    }

    @Override
    protected Class[] getServletConfigClasses() {
        return new Class[] { WebMvcConfiguration.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    @Override
    protected Class[] getRootConfigClasses() {
        return new Class[] {
                ApplicationConfiguration.class,
                WebMvcConfiguration.class,
                WebSecurityConfiguration.class,
                SpringDataConfiguration.class,
                SMTPConfiguration.class,
                SchedulerConfig.class
        };
    }
}
