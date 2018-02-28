package server.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import server.Messages.Message;
import server.Messages.MessageStates;
import server.Model.User;
import server.Services.UserService;

import javax.servlet.http.HttpSession;

public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
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

}
