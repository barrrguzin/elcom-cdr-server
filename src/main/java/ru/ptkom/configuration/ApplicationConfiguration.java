package ru.ptkom.configuration;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ptkom.dao.RoleDAO;
import ru.ptkom.dao.UserDAO;
import ru.ptkom.mapper.ActiveDirectoryGroupToRoleMapper;
import ru.ptkom.service.ConfigurationFIleService;
import ru.ptkom.service.impl.UserDetailsServiceImpl;


@Configuration
@ComponentScan(basePackages = "ru.ptkom")
@PropertySource("classpath:application.properties")
public class ApplicationConfiguration {

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    @DependsOn("propertySourcesPlaceholderConfigurer")
    public ConfigurationFIleService configurationFIleService() {
        return new ConfigurationFIleService();
    }

    @Bean
    public ActiveDirectoryGroupToRoleMapper activeDirectoryGroupToRoleMapper() {
        return new ActiveDirectoryGroupToRoleMapper(configurationFIleService());
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder, RoleDAO roleDAO, UserDAO userDAO) {
        return new UserDetailsServiceImpl(passwordEncoder, roleDAO, userDAO);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}