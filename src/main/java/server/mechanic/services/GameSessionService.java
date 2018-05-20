package server.mechanic.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import server.mechanic.GameInitService;
import server.mechanic.game.GameSession;
import server.mechanic.game.GameUser;
import server.mechanic.game.map.GameMap;
import server.mechanic.messages.outbox.EndGame;
import server.mechanic.services.event.client.ClientEventService;
import server.model.User;
import server.services.UserService;
import server.websocket.RemotePointService;

import javax.validation.constraints.NotNull;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service
public class GameSessionService {
    @Value("${MULTIPLAYER_MAPS_DIR}")
    private String filesDir;
    @Value("${MULTIPLAYER_MAPS_COUNT}")
    private Integer mapsCount;


    private static final Logger LOGGER = LoggerFactory.getLogger(GameSessionService.class);
    @NotNull
    private final Map<Integer, GameSession> usersMap = new HashMap<>();
    @NotNull
    private final Set<GameSession> gameSessions = new LinkedHashSet<>();

    @NotNull
    private final RemotePointService remotePointService;

    @NotNull
    private final GameInitService gameInitService;

    @NotNull
    private final ClientEventService clientEventService;

    @NotNull
    private final UserService userService;

    @Value("${SCORE_WIN}")
    private Integer scoreWin;
    @Value("${SCORE_LOSE}")
    private Integer scoreLose;
    @Value("${SCORE_DRAW}")
    private Integer scoreDraw;


    public GameSessionService(@NotNull RemotePointService remotePointService,
                              @NotNull GameInitService gameInitService,
                              @NotNull ClientEventService clientEventService,
                              @NotNull UserService userService) {
        this.remotePointService = remotePointService;
        this.gameInitService = gameInitService;
        this.clientEventService = clientEventService;
        this.userService = userService;
    }

    public Set<GameSession> getSessions() {
        return gameSessions;
    }

    @Nullable
    public GameSession getSessionForUser(@NotNull Integer userId) {
        return usersMap.get(userId);
    }

    public boolean isPlaying(@NotNull Integer userId) {
        return usersMap.containsKey(userId);
    }

    public void forceTerminate(@NotNull GameSession gameSession, boolean error) {
        gameSessions.remove(gameSession);
        usersMap.remove(gameSession.getFirst().getUserId());
        usersMap.remove(gameSession.getSecond().getUserId());
        clientEventService.clearForUser(gameSession.getFirst().getUserId());
        clientEventService.clearForUser(gameSession.getSecond().getUserId());

        LOGGER.info("Game session " + gameSession.getSessionId() + (error ? " was terminated due to error. " : " was cleaned. ")
                + gameSession.toString());
    }

    public boolean checkHealthState(@NotNull GameSession gameSession) {
        return gameSession.getPlayers().stream().map(GameUser::getUserId).allMatch(remotePointService::isConnected);
    }

    public boolean checkHealthStateAny(@NotNull GameSession gameSession) {
        return gameSession.getPlayers().stream().map(GameUser::getUserId).anyMatch(remotePointService::isConnected);
    }

    public void startGame(@NotNull User first, @NotNull User second) {
        ObjectMapper mapper = new ObjectMapper();
        Integer mapNum  = 1 + (int) (Math.random() * (mapsCount - 1));
        String mapName = "multi_" + mapNum;
        String filePath = filesDir + mapName + ".map";
        GameMap gameMap = null;
        try {
            gameMap = mapper.readValue(new FileInputStream(filePath), GameMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final GameSession gameSession = new GameSession(first, second, gameMap, this);
        gameSessions.add(gameSession);
        usersMap.put(gameSession.getFirst().getUserId(), gameSession);
        usersMap.put(gameSession.getSecond().getUserId(), gameSession);
        gameInitService.initGameFor(gameSession);
        LOGGER.info("Game session " + gameSession.getSessionId() + " started. " + gameSession.toString());
    }

    public void finishGame(@NotNull GameSession gameSession, @NotNull Integer winnerId, String reasonFirst, String reasonSecond) {
        final EndGame firstMessage = new EndGame();
        final EndGame secondMessage = new EndGame();

        firstMessage.setYour(gameSession.getFirst().getScore());
        firstMessage.setOpponent(gameSession.getSecond().getScore());
        secondMessage.setYour(gameSession.getSecond().getScore());
        secondMessage.setOpponent(gameSession.getFirst().getScore());

        firstMessage.setReason(reasonFirst);
        secondMessage.setReason(reasonSecond);

        if (winnerId == null) {
            firstMessage.setResult("DRAW");
            secondMessage.setResult("DRAW");
            userService.increaseScoreById(gameSession.getFirst().getUserId(), scoreDraw);
            userService.increaseScoreById(gameSession.getSecond().getUserId(), scoreDraw);
        } else if (gameSession.getSecond().getUserId().equals(winnerId)) {
            firstMessage.setResult("LOSE");
            secondMessage.setResult("WIN");
            userService.increaseScoreById(gameSession.getFirst().getUserId(), scoreLose);
            userService.increaseScoreById(gameSession.getSecond().getUserId(), scoreWin);
        } else {
            firstMessage.setResult("WIN");
            secondMessage.setResult("LOSE");
            userService.increaseScoreById(gameSession.getFirst().getUserId(), scoreWin);
            userService.increaseScoreById(gameSession.getSecond().getUserId(), scoreLose);
        }

        try {
            remotePointService.sendMessageToUser(gameSession.getFirst().getUserId(), firstMessage);
        } catch (IOException ignored) {
            // for checkstyle
            ignored.getMessage();
        }
        try {
            remotePointService.sendMessageToUser(gameSession.getSecond().getUserId(), secondMessage);
        } catch (IOException ignored) {
            // for checkstyle
            ignored.getMessage();
        }
    }
}
