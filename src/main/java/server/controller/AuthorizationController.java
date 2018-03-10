package server.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.messages.Message;
import server.messages.MessageStates;
import server.model.ChangeUser;
import server.model.User;
import server.services.UserService;

import javax.servlet.http.HttpSession;

@CrossOrigin(origins = "http://127.0.0.1:3000", allowCredentials = "true")
@RestController
public class AuthorizationController {

    private final UserService userService;

    public AuthorizationController(UserService userService) {

        this.userService = userService;
    }

    @PostMapping(value = "/settings", produces = "application/json")
    public ResponseEntity settings(@RequestBody ChangeUser changeUser, HttpSession httpSession) {

        Integer userIdInSession = (Integer) httpSession.getAttribute("blendocu");

        if (userIdInSession == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(MessageStates.UNAUTHORIZED));
        }

        User oldUser = userService.getUserById(userIdInSession);

        // if user was deleted or something went wrong
        if (oldUser == null) {
            httpSession.invalidate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/");
            return new ResponseEntity(headers, HttpStatus.TEMPORARY_REDIRECT);
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(MessageStates.UNAUTHORIZED));
        }

        final Boolean changeLogin;
        final Boolean changeEmail;
        final Boolean changePassword;
        final Boolean changeImage;

        changeLogin = !(changeUser.getLogin() == null
                        || changeUser.getLogin().equals(""));
        changeEmail = !(changeUser.getEmail() == null
                        || changeUser.getEmail().equals(""));
        changeImage = changeUser.getImage() != null;
        changePassword = !(changeUser.getOldPassword() == null
                        || changeUser.getNewPassword() == null
                        || changeUser.getNewPassword().equals(""));

        // Login is already registered
        if (changeLogin) {
            if (userService.isLoginRegistered(changeUser.getLogin())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(MessageStates.LOGIN_ALREADY_EXISTS));
            }
        }

        // Email is already registered
        if (changeEmail) {
            if (userService.isEmailRegistered(changeUser.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(MessageStates.EMAIL_ALREADY_EXISTS));
            }
        }

        if (changeEmail && changeLogin) {
            oldUser.setLogin(changeUser.getLogin());
            oldUser.setEmail(changeUser.getEmail());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Message(MessageStates.CHANGED_USER_DATA));
        }

        if (changeEmail) {
            oldUser.setEmail(changeUser.getEmail());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Message(MessageStates.CHANGED_USER_DATA));
        }

        if (changeLogin) {
            oldUser.setLogin(changeUser.getLogin());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Message(MessageStates.CHANGED_USER_DATA));
        }

        if (changeImage) {
            // TODO Write it when image will be available to download
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Message(MessageStates.CHANGED_USER_DATA));
        }

        if (changePassword) {
            if (changeUser.getOldPassword().equals(oldUser.getPassword())) {
                oldUser.setPassword(changeUser.getNewPassword());
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Message(MessageStates.CHANGED_USER_DATA));
            }
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(MessageStates.NOT_ENOUGH_DATA));
    }

    @PostMapping(value = "/logout", produces = "application/json")
    public ResponseEntity logout(HttpSession httpSession) {

        Integer userIdInSession = (Integer) httpSession.getAttribute("blendocu");

        if (userIdInSession == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(MessageStates.UNAUTHORIZED));
        }

        httpSession.invalidate();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Message(MessageStates.UNAUTHORIZED));
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<Message> login(@RequestBody User user, HttpSession httpSession) {

        Integer userIdInSession = (Integer) httpSession.getAttribute("blendocu");

        if (userIdInSession != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(MessageStates.ALREADY_AUTHORIZED));
        }

        // check whether data is enough to authorize
        String loginOrEmail = user.getLogin();

        if (loginOrEmail == null) {
            loginOrEmail = user.getEmail();
        }

        if (loginOrEmail == null || user.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(MessageStates.NOT_ENOUGH_DATA));
        }

        //authorizing
        Integer userIdInDB = userService.authorizeUser(user);

        if (userIdInDB != null) {
            httpSession.setAttribute("blendocu", userIdInDB);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Message(MessageStates.AUTHORIZED));
        }

        //no such user registered
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Message(MessageStates.BAD_AUTHORIZE));
    }

    @PostMapping(value = "/register", produces = "application/json")
    public ResponseEntity<Message> register(@RequestBody User user, HttpSession httpSession) {
        if (userService.isEmailRegistered(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(MessageStates.EMAIL_ALREADY_EXISTS));
        }

        if (userService.isLoginRegistered(user.getLogin())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(MessageStates.LOGIN_ALREADY_EXISTS));
        }

        httpSession.setAttribute("blendocu", userService.addUser(user));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Message(MessageStates.REGISTERED));
    }

    @GetMapping(value = "/me", produces = "application/json")
    public ResponseEntity<?> me(HttpSession httpSession) {
        Integer userId = (Integer) httpSession.getAttribute("blendocu");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(MessageStates.UNAUTHORIZED));
        }

        User userInDB = userService.checkUserById(userId);
        String userLogin = userInDB.getLogin();
        String userImage = userInDB.getImage();
        System.out.println(userImage);
        System.out.println(userLogin);

        if (userLogin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(MessageStates.UNAUTHORIZED));
        }

        User user = new User();
        user.setLogin(userLogin);
        user.setImage(userImage);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(user);

    }
}

