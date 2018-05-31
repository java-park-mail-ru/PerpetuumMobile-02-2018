package server.mechanic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import server.mechanic.game.GameSession;
import server.mechanic.game.GameUser;
import server.mechanic.messages.outbox.InitGame;
import server.mechanic.services.event.client.ClientEventService;
import server.websocket.RemotePointService;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class GameInitService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientEventService.class);

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
            try {
                remotePointService.sendMessageToUser(player.getUserId(), initMessage);
            } catch (IOException e) {
                players.forEach(playerToCutOff -> remotePointService.cutDownConnection(playerToCutOff.getUserId(),
                        CloseStatus.SERVER_ERROR));
                LOGGER.error("Unnable to start a game", e);
            }
        }
    }

    @SuppressWarnings("TooBroadScope")
    private InitGame.Request createInitMessageFor(@NotNull GameSession gameSession, @NotNull Integer userId) {
        final InitGame.Request initGameMessage = new InitGame.Request();

        initGameMessage.setOpponent(gameSession.getEnemy(userId).getUserProfile().safeGet());

        initGameMessage.setMap(gameSession.getGameMap().safeGet());

        return initGameMessage;
    }
}
