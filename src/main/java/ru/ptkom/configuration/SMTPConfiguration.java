package ru.ptkom.configuration;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ptkom.service.ConfigurationFIleService;

import java.util.Properties;

@Configuration
public class SMTPConfiguration {

    private Boolean smtpAuthentication;
    private String smtpUsername;
    private String smtpPassword;
    private String smtpServerAddress;
    private String smtpServerPort;
    private Boolean debug;
    private Boolean smtpTLS;

    private final ConfigurationFIleService configurationFIleService;

    public SMTPConfiguration(ConfigurationFIleService configurationFIleService) {
        this.configurationFIleService = configurationFIleService;
        initializeConfigurationProperties();
    }

    private void initializeConfigurationProperties() {
       smtpAuthentication = configurationFIleService.getSmtpAuthentication();
       if (smtpAuthentication) {
           smtpUsername = configurationFIleService.getSmtpAuthenticationUsername();
           smtpPassword = configurationFIleService.getSmtpAuthenticationPassword();
       }
       smtpServerAddress = configurationFIleService.getSmtpServerAddress();
       smtpServerPort = configurationFIleService.getSmtpServerPort();
       debug = configurationFIleService.getSmtpDebug();
       smtpTLS = configurationFIleService.getSmtpTLS();
    }

    @Bean(name = "mailSession")
    public Session getMailSession() {
        if (smtpAuthentication) {
            return Session.getInstance(setProperties(), new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUsername, smtpPassword);
                }
            });
        } else {
            return Session.getInstance(setProperties());
        }
    }

    private Properties setProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", smtpAuthentication);
        properties.put("mail.smtp.host", smtpServerAddress);
        properties.put("mail.smtp.port", smtpServerPort);
        properties.put("mail.debug", debug);
        if (smtpTLS) {
            properties.put("mail.smtp.starttls.enable", smtpTLS);
        }
        //properties.put("mail.smtp.ssl.trust", "");
        return properties;
    }

}
