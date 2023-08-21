package ru.ptkom.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.ptkom.configuration.SpringDataConfiguration;

import java.time.LocalDate;

@Service
@PropertySource("classpath:application.properties")
public class ConfigurationFIleService {

    private final static Logger log = LoggerFactory.getLogger(ConfigurationFIleService.class);

    @Value("${default.offset}")
    private Long defaultOffsetValue;

    @Value("${default.onPageQuantity}")
    private Long defaultOnPageQuantityValue;

    @Value("${security.debug}")
    private Boolean securityDebug;

    @Value("${ad.address}")
    private String activeDirectoryIpAddress;

    @Value("${ad.port}")
    private String activeDirectoryPort;

    @Value("${ad.domain}")
    private String activeDirectoryDomain;

    @Value("${ad.role.admin}")
    private String activeDirectoryAdministratorRoleName;

    @Value("${ad.role.user}")
    private String activeDirectoryUserRoleName;

    @Value("${ad.smb.username}")
    private String smbUsername;

    @Value("${ad.smb.password}")
    private String smbPassword;

    @Value("${ad.smb.sharedfolder}")
    private String smbSharedFolder;

    @Value("${ad.smb.address}")
    private String smbIpAddress;

    @Value("${ad.smb.pathtofolder}")
    private String smbPathToFolder;

    @Value("${mail.smtp.auth}")
    private Boolean smtpAuthentication;

    @Value("${mail.smtp.host}")
    private String smtpServerAddress;

    @Value("${mail.smtp.port}")
    private String smtpServerPort;

    @Value("${mail.debug}")
    private Boolean smtpDebug;

    @Value("${mail.smtp.starttls.enable}")
    private Boolean smtpTLS;

    @Value("${mail.smtp.username}")
    private String smtpAuthenticationUsername;

    @Value("${mail.smtp.password}")
    private String smtpAuthenticationPassword;

    @Value("${database.debug}")
    private Boolean hibernateDebug;

    @Value("${database.address}")
    private String databaseAddress;

    @Value("${database.port}")
    private String databasePort;

    @Value("${database.name}")
    private String databaseName;

    @Value("${database.username}")
    private String databaseUsername;

    @Value("${database.password}")
    private String databasePassword;

    private static final LocalDate DEFAULT_START_DATE_VALUE = LocalDate.ofYearDay(1986, 116);

    private static final String KEY_OF_INCOMING_REPORT = "incoming";
    private static final String KEY_OF_OUTGOING_REPORT = "outgoing";

    public Long getDefaultOffsetValue() {
        return defaultOffsetValue;
    }

    public Long getDefaultOnPageQuantityValue() {
        return defaultOnPageQuantityValue;
    }

    public LocalDate getDefaultStartDateValue() {
        return DEFAULT_START_DATE_VALUE;
    }

    public String getKeyOfIncomingReport() {return KEY_OF_INCOMING_REPORT;}

    public String getKeyOfOutgoingReport() {return KEY_OF_OUTGOING_REPORT;}

    public Boolean getSecurityDebug() {
        return securityDebug;
    }

    public String getActiveDirectoryIpAddress() {
        return activeDirectoryIpAddress;
    }

    public String getActiveDirectoryPort() {
        return activeDirectoryPort;
    }

    public String getActiveDirectoryDomain() {
        return activeDirectoryDomain;
    }

    public String getActiveDirectoryAdministratorRoleName() {
        return activeDirectoryAdministratorRoleName;
    }

    public String getActiveDirectoryUserRoleName() {
        return activeDirectoryUserRoleName;
    }

    public Boolean getSmtpAuthentication() {
        return smtpAuthentication;
    }

    public String getSmtpServerAddress() {
        return smtpServerAddress;
    }

    public String getSmtpServerPort() {
        return smtpServerPort;
    }

    public Boolean getSmtpDebug() {
        return smtpDebug;
    }

    public Boolean getSmtpTLS() {
        return smtpTLS;
    }

    public String getSmtpAuthenticationUsername() {
        return smtpAuthenticationUsername;
    }

    public String getSmtpAuthenticationPassword() {
        return smtpAuthenticationPassword;
    }

    public Boolean getHibernateDebug() {
        return hibernateDebug;
    }

    public String getDatabaseAddress() {
        return databaseAddress;
    }

    public String getDatabasePort() {
        return databasePort;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public String getSmbUsername() {
        return smbUsername;
    }

    public String getSmbPassword() {
        return smbPassword;
    }

    public String getSmbSharedFolder() {
        return smbSharedFolder;
    }

    public String getSmbIpAddress() {
        return smbIpAddress;
    }

    public String getSmbPathToFolder() {
        return smbPathToFolder;
    }
}
