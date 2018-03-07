package server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.messages.Message;
import server.messages.MessageStates;
import server.model.User;
import server.model.UserRegister;
import server.services.UserService;

import javax.servlet.http.HttpSession;

@CrossOrigin(origins = "http://127.0.0.1:3000", allowCredentials = "true")
@RestController
public class AuthorizationController {

    private final UserService userService;

    public AuthorizationController(UserService userService) {

        this.userService = userService;
    }

    @GetMapping(value = "/me", produces = "application/json")
    public ResponseEntity whoAmI(HttpSession httpSession) {

        Integer userIdInSession = (Integer) httpSession.getAttribute("blendocu");

        User requestedUser = userService.getUserById(userIdInSession);

        if (requestedUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(MessageStates.UNAUTHORIZED));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new User(requestedUser.getLogin(), null, null, requestedUser.getScore()));
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<Message> login(@RequestBody User user, HttpSession httpSession) {

        Integer userIdInSession = (Integer) httpSession.getAttribute("blendocu");

        if (userIdInSession != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(MessageStates.ALREADY_AUTHORIZED));
        }


        //part of universal code: email or login. front must validate.

        String loginOrEmail = user.getLogin();

        if (loginOrEmail == null) {
            loginOrEmail = user.getEmail();
        }

        if (loginOrEmail == null || user.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(MessageStates.NOT_ENOUGH_DATA));
        }

        Integer userIdInDB = userService.authorizeUserByEmail(user);

        if (userIdInDB != null) {
            httpSession.setAttribute("blendocu", userIdInDB);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Message(MessageStates.AUTHORIZED));
        }

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

        userService.addUser(user);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Message(MessageStates.REGISTERED));
    }

    @GetMapping(value = "/me", produces = "application/json")
    public ResponseEntity<?> me(HttpSession httpSession) {
        Integer userId = (Integer) httpSession.getAttribute("blendocu");
        System.out.println(userId);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(MessageStates.UNAUTHORIZED));
        }

        String userLogin = userService.checkUserById(userId);
        if (userLogin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(MessageStates.UNAUTHORIZED));
        }

        User user = new User();
        user.setLogin(userLogin);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(user);

    }
}

