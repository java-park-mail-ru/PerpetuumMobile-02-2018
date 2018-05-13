package server.mechanic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import server.mechanic.game.GameSession;
import server.mechanic.game.GameUser;
import server.mechanic.messages.outbox.InitGame;
import server.model.User;
import server.websocket.RemotePointService;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class GameInitService {
//    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSnapshotService.class);

    @NotNull
    private final RemotePointService remotePointService;

    public GameInitService(@NotNull RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
    }

    public void initGameFor(@NotNull GameSession gameSession) {
        final Collection<GameUser> players = new ArrayList<>();
        players.add(gameSession.getFirst());
        players.add(gameSession.getSecond());
        for (GameUser player : players) {
            final InitGame.Request initMessage = createInitMessageFor(gameSession, player.getUserId());
            //noinspection OverlyBroadCatchBlock
            try {
                remotePointService.sendMessageToUser(player.getUserId(), initMessage);
            } catch (IOException e) {
                // TODO: Reentrance mechanism
                players.forEach(playerToCutOff -> remotePointService.cutDownConnection(playerToCutOff.getUserId(),
                        CloseStatus.SERVER_ERROR));
//                LOGGER.error("Unnable to start a game", e);
            }
        }
    }

    @SuppressWarnings("TooBroadScope")
    private InitGame.Request createInitMessageFor(@NotNull GameSession gameSession, @NotNull Integer userId) {
        final InitGame.Request initGameMessage = new InitGame.Request();

//        final Map<Id<User>, GameUser.ServerPlayerSnap> playerSnaps = new HashMap<>();
        final Map<Integer, String> names = new HashMap<>();
        final Map<Integer, String> colors = new HashMap<>();

        final Collection<GameUser> players = new ArrayList<>();
        players.add(gameSession.getFirst());
        players.add(gameSession.getSecond());
        for (GameUser player : players) {
//            playerSnaps.put(player.getUserId(), player.getSnap());
            names.put(player.getUserId(), player.getUserProfile().getLogin());
        }

//        colors.put(userId, Config.SELF_COLOR);
//        colors.put(gameSession.getEnemy(userId).getUserId(), Config.ENEMY_COLOR);

        initGameMessage.setSelf(userId);
        initGameMessage.setOpponent(gameSession.getEnemy(userId).getUserId());
        initGameMessage.setNames(names);
//        initGameMessage.setColors(colors);
//        initGameMessage.setPlayers(playerSnaps);

//        initGameMessage.setBoard(gameSession.getBoard().getSnap());
        return initGameMessage;
    }
}
