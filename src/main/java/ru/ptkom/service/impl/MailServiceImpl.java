package ru.ptkom.service.impl;

import com.sun.istack.NotNull;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.ptkom.service.ConfigurationFIleService;
import ru.ptkom.service.MaliService;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MailServiceImpl implements MaliService {

    private static String INCOMING;
    private static String OUTGOING;

    private static final String TABLE_HEAD = "<table><thead><tr><th><strong>Оператор:</strong></th><th><strong>Входящие:</strong></th><th><strong>Исходящие:</strong></th></tr></thead><tbody>";

    @Qualifier("mailSession")
    private final Session mailSession;
    private final ConfigurationFIleService configurationFIleService;

    public MailServiceImpl(Session mailSession, ConfigurationFIleService configurationFIleService) {
        this.mailSession = mailSession;
        this.configurationFIleService = configurationFIleService;
        INCOMING = this.configurationFIleService.getKeyOfIncomingReport();
        OUTGOING = this.configurationFIleService.getKeyOfOutgoingReport();
    }

    @Override
    public void sendReportByEmail(Set<String> emails, Map<String, Map<String, Long>> reportData, String startDateTime, String endDateTime) {
        try {
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress("monitor@ptkom.ru"));
            message.setRecipients(Message.RecipientType.TO, makeRecipients(emails));
            message.setSubject("Отчет: " + startDateTime + " - " + endDateTime);
            String messageText = makeMessageBodyText(reportData);
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(messageText, "text/html; charset=utf-8");
            Multipart multipart = new MimeMultipart();
//            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
//            attachmentBodyPart.attachFile(new File("path/to/file"));
//            multipart.addBodyPart(attachmentBodyPart);
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send email to: " + convertRecipientsToString(emails) + "; Reason: " + e.getMessage());
        }
    }

    private InternetAddress[] makeRecipients(Collection<String> emails) {
        return emails.stream().map(recipient -> {
            try {
                return new InternetAddress(recipient);
            } catch (AddressException e) {
                return null;
            }
        })
                .filter(Objects::nonNull)
                .toArray(size -> new InternetAddress[size]);
    }

    private String convertRecipientsToString(Set<String> recipients) {
        return recipients.stream().collect(Collectors.joining("; ", "{", "}"));
    }

    private String makeMessageBodyText(Map<String, Map<String, Long>> reportData) {
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("<html>");
        resultBuilder.append("<head>");
        setStyle(resultBuilder);
        resultBuilder.append("</head>");
        resultBuilder.append("<body>");
        resultBuilder.append(TABLE_HEAD);
        if (reportData.get(INCOMING).keySet().size() == reportData.get(OUTGOING).keySet().size()) {
            Set<String> keys = reportData.get(INCOMING).keySet();
            keys.forEach(operator -> {
                resultBuilder.append("<tr><td><strong>").append(operator).append("</strong></td><td>").append(reportData.get(INCOMING).get(operator)).append("</td><td>").append(reportData.get(OUTGOING).get(operator));
            });
            return resultBuilder.append("</tbody></table>").append("</body>").append("</html>").toString();
        } else {
            throw new RuntimeException("Operator quantity on incoming call list is not equal to outgoing calls value");
        }
    }

    private void setStyle(StringBuilder template) {
        template.append("<style>");
        template.append("table, th, td{\n" +
                "  border: 1px solid;\n" +
                "  padding: 12px 15px;\n" +
                "  text-align: left;\n" +
                "  margin: 25px 0;\n" +
                "  width: 100%;\n" +
                "}");
        template.append("</style>");
    }
}
