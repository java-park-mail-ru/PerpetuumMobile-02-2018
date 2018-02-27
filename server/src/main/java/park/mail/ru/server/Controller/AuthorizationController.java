package park.mail.ru.server.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import park.mail.ru.server.Messages.Message;
import park.mail.ru.server.Model.User;

import javax.servlet.http.HttpSession;


@RestController
public class AuthorizationController {

    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestBody User user, HttpSession httpSession) {

        Integer userIdInSession = (Integer) httpSession.getAttribute("id");

        if (userIdInSession != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Message.ALREADY_AUTHORIZED);
        }

        String loginOrEmail = user.getLogin();

        if (loginOrEmail == null) {
            loginOrEmail = user.getEmail();
        }

        if(isUserExist(loginOrEmail))


    }
}
