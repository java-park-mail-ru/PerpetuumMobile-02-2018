package server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class JavaMailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaMailService.class);

    @Autowired
    private JavaMailSender sender;

    public void sendEmail(String from, String to, String subj, String text) throws Exception {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(from);
        helper.setTo(to);
        helper.setText(text, true);
        helper.setSubject(subj);

        try {
            sender.send(message);
        } catch (MailException e) {
            LOGGER.error("Can't send email: " + e.getMessage());
        }
    }
}
