package server.mechanic.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import server.mechanic.GameInitService;
import server.mechanic.game.GameSession;
import server.mechanic.game.GameUser;
import server.mechanic.game.map.GameMap;
import server.mechanic.services.event.client.ClientEventService;
import server.model.User;
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

//    @NotNull
//    private final MechanicsTimeService timeService;
//
    @NotNull
    private final GameInitService gameInitService;

//    @NotNull
//    private final GameTaskScheduler gameTaskScheduler;

    @NotNull
    private final ClientEventService clientEventService;


    public GameSessionService(@NotNull RemotePointService remotePointService,
//                              @NotNull MechanicsTimeService timeService,
                              @NotNull GameInitService gameInitService,
//                              @NotNull GameTaskScheduler gameTaskScheduler,
                              @NotNull ClientEventService clientEventService
        ) {
        this.remotePointService = remotePointService;
//        this.timeService = timeService;
        this.gameInitService = gameInitService;
//        this.gameTaskScheduler = gameTaskScheduler;
        this.clientEventService = clientEventService;
//        this.shuffler = shuffler;
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
        final boolean exists = gameSessions.remove(gameSession);
        gameSession.setFinished();
        usersMap.remove(gameSession.getFirst().getUserId());
        usersMap.remove(gameSession.getSecond().getUserId());
        final CloseStatus status = error ? CloseStatus.SERVER_ERROR : CloseStatus.NORMAL;
        if (exists) {
            remotePointService.cutDownConnection(gameSession.getFirst().getUserId(), status);
            remotePointService.cutDownConnection(gameSession.getSecond().getUserId(), status);
        }
        clientEventService.clearForUser(gameSession.getFirst().getUserId());
        clientEventService.clearForUser(gameSession.getSecond().getUserId());

        LOGGER.info("Game session " + gameSession.getSessionId() + (error ? " was terminated due to error. " : " was cleaned. ")
                + gameSession.toString());
    }

    public boolean checkHealthState(@NotNull GameSession gameSession) {
        return gameSession.getPlayers().stream().map(GameUser::getUserId).allMatch(remotePointService::isConnected);
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

        final GameSession gameSession = new GameSession(first, second, gameMap, this);//, timeService, shuffler);
        gameSessions.add(gameSession);
        usersMap.put(gameSession.getFirst().getUserId(), gameSession);
        usersMap.put(gameSession.getSecond().getUserId(), gameSession);
//        gameSession.getBoard().randomSwap();
        gameInitService.initGameFor(gameSession);
//        gameTaskScheduler.schedule(Config.START_SWITCH_DELAY, new SwapTask(gameSession, gameTaskScheduler, Config.START_SWITCH_DELAY));
        LOGGER.info("Game session " + gameSession.getSessionId() + " started. " + gameSession.toString());
    }

//
//public void finishGame(@NotNull GameSession gameSession) {
//        gameSession.setFinished();
//        final FinishGame.Overcome firstOvercome;
//        final FinishGame.Overcome secondOvercome;
//        final int firstScore = gameSession.getFirst().claimPart(MechanicPart.class).getScore();
//        final int secondScore = gameSession.getSecond().claimPart(MechanicPart.class).getScore();
//        if (firstScore == secondScore) {
//            firstOvercome = FinishGame.Overcome.DRAW;
//            secondOvercome = FinishGame.Overcome.DRAW;
//        } else if (firstScore > secondScore) {
//            firstOvercome = FinishGame.Overcome.WIN;
//            secondOvercome = FinishGame.Overcome.LOSE;
//        } else {
//            firstOvercome = FinishGame.Overcome.LOSE;
//            secondOvercome = FinishGame.Overcome.WIN;
//        }
//
//        try {
//            remotePointService.sendMessageToUser(gameSession.getFirst().getUserId(), new FinishGame(firstOvercome));
//        } catch (IOException ex) {
//            LOGGER.warn(String.format("Failed to send FinishGame message to user %s",
//                    gameSession.getFirst().getUserProfile().getLogin()), ex);
//        }
//
//        try {
//            remotePointService.sendMessageToUser(gameSession.getSecond().getUserId(), new FinishGame(secondOvercome));
//        } catch (IOException ex) {
//            LOGGER.warn(String.format("Failed to send FinishGame message to user %s",
//                    gameSession.getSecond().getUserProfile().getLogin()), ex);
//        }
//    }
//    private static final class SwapTask extends GameTaskScheduler.GameSessionTask {
//
//        private final GameTaskScheduler gameTaskScheduler;
//        private final long currentDelay;
//
//        private SwapTask(GameSession gameSession, GameTaskScheduler gameTaskScheduler, long currentDelay) {
//            super(gameSession);
//            this.gameTaskScheduler = gameTaskScheduler;
//            this.currentDelay = currentDelay;
//        }

//        @Override
//        public void operate() {
//            if (getGameSession().isFinished()) {
//                return;
//            }
//            getGameSession().getBoard().randomSwap();
//            final long newDelay = Math.max(currentDelay - Config.SWITCH_DELTA, Config.SWITCH_DELAY_MIN);
//            gameTaskScheduler.schedule(newDelay,
//                    new SwapTask(getGameSession(), gameTaskScheduler, newDelay));
//        }
//    }

}
