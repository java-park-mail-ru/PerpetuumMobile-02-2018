package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import server.mechanic.GameMechanics;
import server.mechanic.game.GameSession;
import server.mechanic.messages.inbox.AliceSetCubic;
import server.mechanic.messages.inbox.SetCubic;
import server.mechanic.services.GameSessionService;
import server.model.alice.AliceIn;
import server.model.alice.AliceOut;
import server.model.alice.AliceResponse;
import server.model.alice.AliceSessionOut;
import server.services.UserService;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class AliceService {

    private final UserService userService;
    private final GameSessionService gameSessionService;
    private final GameMechanics gameMechanics;

    private HashMap<String, Integer> players = new HashMap<>();

    @Autowired
    public AliceService(UserService userService, GameSessionService gameSessionService, GameMechanics gameMechanics) {
        this.userService = userService;
        this.gameSessionService = gameSessionService;
        this.gameMechanics = gameMechanics;
    }

    @PostMapping(value = "/alice-webhooks", produces = "application/json")
    public ResponseEntity aliceWebhooks(@RequestBody AliceIn aliceIn) {

        if (!players.containsKey(aliceIn.getSession().getSessionId())) {

            final Pattern auth = Pattern.compile("^мой токен ([0-9]+)$");
            final Matcher authMatcher = auth.matcher(aliceIn.getRequest().getCommand());
            if (authMatcher.matches()) {
                final String tokenStr = authMatcher.group(1);
                final Integer token = Integer.parseInt(tokenStr);
                final GameSession gameSession = gameSessionService.getSessionForUser(token);
                if (gameSession == null) {
                    return ResponseEntity.status(HttpStatus.OK).body(notExistingToken(aliceIn));
                }
                players.put(aliceIn.getSession().getSessionId(), token);
                return ResponseEntity.status(HttpStatus.OK).body(authorized(aliceIn));
            }
            return ResponseEntity.status(HttpStatus.OK).body(notAuthorized(aliceIn));
        }

        final Pattern aliceSetCubicPattern = Pattern.compile("^поставь кубик ([0-9]+) на место ([0-9]+)$");
        final Matcher aliceSetCubicMatcher = aliceSetCubicPattern.matcher(aliceIn.getRequest().getCommand());
        if (aliceSetCubicMatcher.matches()) {
            final Integer cubicId = Integer.parseInt(aliceSetCubicMatcher.group(1));
            final Integer place = Integer.parseInt(aliceSetCubicMatcher.group(2));

            final AliceSetCubic aliceSetCubic = new AliceSetCubic();
            aliceSetCubic.setCubicId(cubicId);
            aliceSetCubic.setPlace(place);

            gameMechanics.addClientEvent(players.get(aliceIn.getSession().getSessionId()), aliceSetCubic);
            return ResponseEntity.status(HttpStatus.OK).body(done(aliceIn));
        }

        final AliceOut aliceOut = new AliceOut();
        aliceOut.setSession(new AliceSessionOut(aliceIn.getSession()));
        aliceOut.setVersion(aliceIn.getVersion());
        final AliceResponse aliceResponse = new AliceResponse();
        aliceResponse.setText("Сыграем? Можете сказать мне, куда поставить кубик за вас.");
        aliceResponse.setEndSession(false);
        aliceOut.setResponse(aliceResponse);
        return ResponseEntity.status(HttpStatus.OK).body(aliceOut);
    }

    AliceOut notExistingToken(AliceIn aliceIn) {
        final AliceOut aliceOut = new AliceOut();
        aliceOut.setSession(new AliceSessionOut(aliceIn.getSession()));
        aliceOut.setVersion(aliceIn.getVersion());
        final AliceResponse aliceResponse = new AliceResponse();
        aliceResponse.setText("Кажется, такой сейчас не играет.\nПопробуете ещё раз?");
        aliceResponse.setEndSession(false);
        aliceOut.setResponse(aliceResponse);
        return aliceOut;
    }

    AliceOut done(AliceIn aliceIn) {
        final AliceOut aliceOut = new AliceOut();
        aliceOut.setSession(new AliceSessionOut(aliceIn.getSession()));
        aliceOut.setVersion(aliceIn.getVersion());
        final AliceResponse aliceResponse = new AliceResponse();
        aliceResponse.setText("Готово!");
        aliceResponse.setEndSession(false);
        aliceOut.setResponse(aliceResponse);
        return aliceOut;
    }

    AliceOut notAuthorized(AliceIn aliceIn) {
        final AliceOut aliceOut = new AliceOut();
        aliceOut.setSession(new AliceSessionOut(aliceIn.getSession()));
        aliceOut.setVersion(aliceIn.getVersion());
        final AliceResponse aliceResponse = new AliceResponse();
        aliceResponse.setText("Пожалуйста, сначала представьтесь.\nСкажите мне свой токен.");
        aliceResponse.setEndSession(false);
        aliceOut.setResponse(aliceResponse);
        return aliceOut;
    }

    AliceOut authorized(AliceIn aliceIn) {
        final AliceOut aliceOut = new AliceOut();
        aliceOut.setSession(new AliceSessionOut(aliceIn.getSession()));
        aliceOut.setVersion(aliceIn.getVersion());
        final AliceResponse aliceResponse = new AliceResponse();
        aliceResponse.setText("Нашла! Вы в игре!");
        aliceResponse.setEndSession(false);
        aliceOut.setResponse(aliceResponse);
        return aliceOut;
    }
}
