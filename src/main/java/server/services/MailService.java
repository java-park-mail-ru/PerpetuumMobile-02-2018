package server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final JavaMailService javaMailService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);


    @Value("${EMAIL_NOREPLY}")
    private String emailNoreply;

    public MailService(JavaMailService javaMailService) {
        this.javaMailService = javaMailService;
    }

    public void sendChangeEmailMessage(String newEmail, String oldEmail) {
        String msg = String.format("<h3>Your e-mail in <a href='https://blendocu.com'>Blendocu</a> has been changed.</h3>"
                + "Your new e-mail: %s<br><br><i>Best regards,</i><br>Blendocu Team.", newEmail);
        try {
            javaMailService.sendEmail(emailNoreply, newEmail, "E-mail changing.", msg);
            javaMailService.sendEmail(emailNoreply, oldEmail, "E-mail changing.", msg);
        } catch (Exception e) {
            LOGGER.error("Can't send e-mail about e-mail changing");
        }
    }

    public void sendChangeLoginMessage(String email, String login) {
        String msg = String.format("<h3>Your login in <a href='https://blendocu.com'>Blendocu</a> has been changed.</h3>"
                + "Your new login: %s<br><br><i>Best regards,</i><br>Blendocu Team.", login);
        try {
            javaMailService.sendEmail(emailNoreply, email, "Login changing.", msg);
        } catch (Exception e) {
            LOGGER.error("Can't send e-mail about login changing");
        }
    }

    public void sendChangePasswordMessage(String email, String password) {
        String msg = String.format("<h3>Your password in <a href='https://blendocu.com'>Blendocu</a> has been changed.</h3>"
                + "Your new password: %s<br><br><i>Best regards,</i><br>Blendocu Team.", password);
        try {
            javaMailService.sendEmail(emailNoreply, email, "Password changing.", msg);
        } catch (Exception e) {
            LOGGER.error("Can't send e-mail about password changing");
        }
    }

    public void sendRegistrationMessage(String email, String login, String password) {
        String msg = String.format("<h1>We are glad to see you in <a href='https://blendocu.com'>Blendocu</a>.</h1>"
                        + "<h3>Registration is successfully completed.</h3>"
                        + "Your credentials are:<br>Login: %s<br>Password: %s<br><br><i>Best regards,</i><br>Blendocu Team.",
                login, password);
        try {
            javaMailService.sendEmail(emailNoreply, email, "Welcome to Blendocu, " + login + "!", msg);
        } catch (Exception e) {
            LOGGER.error("Can't send e-mail after registration");
        }
    }

    public void sendPassResetMessage(String email, String password) {
        String msg = String.format("<h3>Your password in <a href='https://blendocu.com'>Blendocu</a> has been changed.</h3>"
                + "Your password: %s<br><br><i>Best regards,</i><br>Blendocu Team.", password);
        try {
            javaMailService.sendEmail(emailNoreply, email, "Password reset.", msg);
        } catch (Exception e) {
            LOGGER.error("Can't send e-mail about password reset");
        }
    }
}
