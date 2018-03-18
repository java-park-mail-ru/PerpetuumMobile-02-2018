package server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.messages.Message;
import server.messages.MessageStates;
import server.model.Paginator;
import server.model.Scoreboard;
import server.model.User;
import server.services.UserService;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ScoreboardController {

    private final UserService userService;

    public ScoreboardController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/users", produces = "application/json")
    public ResponseEntity scoreboard(@RequestBody Scoreboard pageNum, HttpSession httpSession) {
        Integer pageNumber = Integer.parseInt(pageNum.getPage());
        Integer onOnePage = 10;
        Integer to = pageNumber * onOnePage;

        if (pageNumber < 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(MessageStates.BAD_DATA));
        }

        List<User> usersFromDb = userService.getAllUsers();

        to = to > usersFromDb.size() ? usersFromDb.size() : to;

        usersFromDb.sort((user1, user2) -> user2.getScore() - user1.getScore());

        Integer from = (pageNumber - 1) * onOnePage;

        Integer maxPageNum;
        maxPageNum = (usersFromDb.size() + (onOnePage - 1)) / onOnePage;

        usersFromDb = usersFromDb.subList(from, to);

        List<User> users = new ArrayList<>();

        for (User copyLoginScore : usersFromDb) {
            users.add(new User(copyLoginScore.getLogin(), "", "", copyLoginScore.getScore()));
        }

        Paginator<List<User>> paginator = new Paginator<>(maxPageNum, users);

        return ResponseEntity.status(HttpStatus.OK).body(paginator);
    }
}
