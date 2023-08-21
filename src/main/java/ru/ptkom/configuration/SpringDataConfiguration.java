package ru.ptkom.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.ptkom.service.ConfigurationFIleService;


import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("ru.ptkom.repository")
@DependsOn("configurationFIleService")
public class SpringDataConfiguration {

    private final static Logger log = LoggerFactory.getLogger(SpringDataConfiguration.class);

    private final ConfigurationFIleService configurationFIleService;

    private final static String DATABASE_URL_TEMPLATE = "jdbc:postgresql://%s:%s/%s";
    private final static String DATABASE_DRIVER_CLASS_NAME = "org.postgresql.Driver";

    private Boolean debug;
    private String databaseServer;
    private String databasePort;
    private String databaseName;
    private String databaseUsername;
    private String databasePassword;

    public SpringDataConfiguration(ConfigurationFIleService configurationFIleService) {
        this.configurationFIleService = configurationFIleService;
        initializeConfigurationProperties();
    }

    private void initializeConfigurationProperties() {
        debug = configurationFIleService.getHibernateDebug();
        databaseServer = configurationFIleService.getDatabaseAddress();
        databasePort = configurationFIleService.getDatabasePort();
        databaseName = configurationFIleService.getDatabaseName();
        databaseUsername = configurationFIleService.getDatabaseUsername();
        databasePassword = configurationFIleService.getDatabasePassword();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManager
                = new LocalContainerEntityManagerFactoryBean();
        entityManager.setDataSource(dataSource());
        entityManager.setPackagesToScan("ru.ptkom.model");
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManager.setJpaVendorAdapter(vendorAdapter);
        entityManager.setJpaProperties(additionalProperties());
        return entityManager;
    }

    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DATABASE_DRIVER_CLASS_NAME);
        dataSource.setUrl(String.format(DATABASE_URL_TEMPLATE, databaseServer, databasePort, databaseName));
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(databasePassword);
        return dataSource;
    }

    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        if (debug) {
            properties.setProperty("hibernate.order_updates", "true");
            properties.setProperty("hibernate.order_inserts", "true");
            properties.setProperty("hibernate.show_sql", "true");
            properties.setProperty("hibernate.format_sql", "true");
            properties.setProperty("hibernate.use_sql_comments", "true");
            properties.setProperty("hibernate.jdbc.batch_size", "1000");
        }
        return properties;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }
}
