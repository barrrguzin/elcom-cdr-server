package ru.ptkom.configuration;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.util.LineInputStream;
import jakarta.mail.util.LineOutputStream;
import jakarta.mail.util.StreamProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

@Configuration
public class SMTPConfiguration {

    private Boolean authentication = false;
    private String username = "";
    private String password = "";


    @Bean(name = "mailSession")
    public Session getMailSession() {
        if (authentication) {
            return Session.getInstance(setProperties(), new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        } else {
            return Session.getInstance(setProperties());
        }
    }

    private Properties setProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", false);
        properties.put("mail.smtp.host", "mail.ptkom.ru");
        properties.put("mail.smtp.port", "25");
        //properties.put("mail.debug", "true");
        //properties.put("mail.smtp.ssl.trust", "");
        //properties.put("mail.smtp.starttls.enable", "false");
        return properties;
    }

}
