package server.controller;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import server.model.User;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://127.0.0.1:3000", allowCredentials = "true")
@RestController
public class ScoreboardController {

    @GetMapping(value = "/users", produces = "application/json")
    public List<User> scoreboard(HttpSession httpSession) {
        List<User> users = new ArrayList<>();
        users.add(new User("her", "her@mail.ru", "her"));
        users.add(new User("her", "her@mail.ru", "her"));
        return users;
    }
}
