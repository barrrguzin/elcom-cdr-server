package ru.ptkom.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

@Configuration
public class SpringWebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {


    @Override
    protected Class[] getServletConfigClasses() {
        return new Class[] { ApplicationConfiguration.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    @Override
    protected Class[] getRootConfigClasses() {
        return new Class[] {};
    }
}
