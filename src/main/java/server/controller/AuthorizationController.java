package server.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import server.messages.Message;
import server.messages.MessageStates;
import server.model.ChangeUser;
import server.model.User;
import server.model.UserAuth;
import server.services.MailService;
import server.services.UserService;

import javax.servlet.http.HttpSession;

@RestController
public class AuthorizationController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;


    public AuthorizationController(UserService userService, PasswordEncoder passwordEncoder, MailService mailService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    public UserService getUserService() {
        return userService;
    }

    @PostMapping(value = "/settings", produces = "application/json")
    public ResponseEntity settings(@RequestBody ChangeUser changeUser, HttpSession httpSession) {

        Integer userIdInSession = (Integer) httpSession.getAttribute("blendocu");

        if (userIdInSession == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(MessageStates.UNAUTHORIZED.getMessage()));
        }

        User oldUser = userService.getUserById(userIdInSession);

        // if user was deleted or something went wrong
        if (oldUser == null) {
            httpSession.invalidate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/");
            return new ResponseEntity(headers, HttpStatus.TEMPORARY_REDIRECT);
        }

        if (!passwordEncoder.matches(changeUser.getOldPassword(), oldUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(MessageStates.BAD_PASSWORD.getMessage()));
        }


        final Boolean changeLogin;
        final Boolean changeEmail;
        final Boolean changePassword;
        final Boolean changeImage;

        changeLogin = !(StringUtils.isEmpty(changeUser.getLogin()));
        changeEmail = !(StringUtils.isEmpty(changeUser.getEmail()));
        //  changeImage = changeUser.getImage() != null;
        changePassword = !(StringUtils.isEmpty(changeUser.getOldPassword())
                        || StringUtils.isEmpty(changeUser.getNewPassword()));

        // Login is already registered
        if (changeLogin) {
            if (userService.isLoginRegistered(changeUser.getLogin())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(MessageStates.LOGIN_ALREADY_EXISTS.getMessage()));
            }
        }

        // Email is already registered
        if (changeEmail) {
            if (userService.isEmailRegistered(changeUser.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(MessageStates.EMAIL_ALREADY_EXISTS.getMessage()));
            }
        }

        if (changeEmail && changeLogin) {
            oldUser.setLogin(changeUser.getLogin());
            oldUser.setEmail(changeUser.getEmail());
            userService.updateUser(oldUser);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Message(MessageStates.CHANGED_USER_DATA.getMessage()));
        }

        if (changeEmail) {
            String oldEmail = oldUser.getEmail();
            oldUser.setEmail(changeUser.getEmail());
            userService.updateUser(oldUser);
            mailService.sendChangeEmailMessage(oldUser.getEmail(), oldEmail);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Message(MessageStates.CHANGED_USER_DATA.getMessage()));
        }

        if (changeLogin) {
            oldUser.setLogin(changeUser.getLogin());
            userService.updateUser(oldUser);
            mailService.sendChangeLoginMessage(oldUser.getEmail(), oldUser.getLogin());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Message(MessageStates.CHANGED_USER_DATA.getMessage()));
        }

        if (changePassword) {
            if (passwordEncoder.matches(changeUser.getOldPassword(), oldUser.getPassword())) {
                oldUser.setPassword(changeUser.getNewPassword());
                boolean updateStatus = userService.updateUserPassword(oldUser);
                if (!updateStatus) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new Message(MessageStates.DATABASE_ERROR.getMessage()));
                }
                mailService.sendChangePasswordMessage(oldUser.getEmail(), changeUser.getNewPassword());
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Message(MessageStates.CHANGED_USER_DATA.getMessage()));
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(MessageStates.NOT_ENOUGH_DATA.getMessage()));
    }

    @PostMapping(value = "/logout", produces = "application/json")
    public ResponseEntity logout(HttpSession httpSession) {

        Integer userIdInSession = (Integer) httpSession.getAttribute("blendocu");

        if (userIdInSession == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(MessageStates.UNAUTHORIZED.getMessage()));
        }

        httpSession.invalidate();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Message(MessageStates.UNAUTHORIZED.getMessage()));
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<Message> login(@RequestBody UserAuth userAuth, HttpSession httpSession) {

        Integer userIdInSession = (Integer) httpSession.getAttribute("blendocu");

        if (userIdInSession != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(MessageStates.ALREADY_AUTHORIZED.getMessage()));
        }

        // check whether data is enough to authorize
        String login = userAuth.getLogin();

        if (login == null || userAuth.getPassword() == null || userAuth.getPassword().equals("")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(MessageStates.NOT_ENOUGH_DATA.getMessage()));
        }


        //authorizing
        Integer userIdInDB = userService.authorizeUser(userAuth);

        if (userIdInDB != null) {
            httpSession.setAttribute("blendocu", userIdInDB);
            httpSession.setMaxInactiveInterval(21600);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Message(MessageStates.AUTHORIZED.getMessage()));
        }

        //no such user registered
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Message(MessageStates.BAD_AUTHORIZE.getMessage()));
    }

    @PostMapping(value = "/register", produces = "application/json")
    public ResponseEntity<Message> register(@RequestBody User user, HttpSession httpSession) {
        user.setImage("no_avatar.png");

        if (userService.isEmailRegistered(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(MessageStates.EMAIL_ALREADY_EXISTS.getMessage()));
        }

        if (userService.isLoginRegistered(user.getLogin())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(MessageStates.LOGIN_ALREADY_EXISTS.getMessage()));
        }
        user.setScore(0);
        httpSession.setAttribute("blendocu", userService.addUser(user));
        httpSession.setMaxInactiveInterval(21600);
        mailService.sendRegistrationMessage(user.getEmail(), user.getLogin(), user.getPassword());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Message(MessageStates.REGISTERED.getMessage()));
    }

    @GetMapping(value = "/me", produces = "application/json")
    public ResponseEntity<?> me(HttpSession httpSession) {
        Integer userId = (Integer) httpSession.getAttribute("blendocu");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(MessageStates.UNAUTHORIZED.getMessage()));
        }

        User userInDB = userService.getUserById(userId);
        if (userInDB == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(MessageStates.UNAUTHORIZED.getMessage()));
        }
        String userLogin = userInDB.getLogin();

        if (userLogin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(MessageStates.UNAUTHORIZED.getMessage()));
        }

        String userImage = userInDB.getImage();
        String userEmail = userInDB.getEmail();
        Integer userToken = userInDB.getId();
        User user = new User();
        user.setToken(userToken);
        user.setLogin(userLogin);
        user.setImage(userImage);
        user.setEmail(userEmail);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(user);

    }

    /**
     * Reset user password by email and send new password to user email.
     *
     * @param user email
     * @return status
     */
    @PostMapping(value = "/reset", produces = "application/json")
    public ResponseEntity<Message> resetPassword(@RequestBody User user) {
        User resetUser = userService.getUserByEmail(user.getEmail());
        if (resetUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message(MessageStates.EMAIL_NOT_FOUND.getMessage()));
        }

        StringBuilder newPass = new StringBuilder();
        String symbSet = "abcdefghijklmnopqrstuvwxyz"
                + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789";
        int iter;
        int passLen = 9;
        for (iter = 0; iter < passLen; iter++) {
            Integer ind  = (int) (Math.random() * symbSet.length());
            newPass.append(symbSet.charAt(ind));
        }
        String newPassword = newPass.toString();

        resetUser.setPassword(newPassword);
        userService.updateUserPassword(resetUser);
        mailService.sendPassResetMessage(user.getEmail(), newPassword);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Message(MessageStates.PASSWORD_CHANGED.getMessage()));
    }
}

