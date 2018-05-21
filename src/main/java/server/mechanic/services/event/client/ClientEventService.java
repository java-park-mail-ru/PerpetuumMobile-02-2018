package server.mechanic.services.event.client;

import org.springframework.stereotype.Service;
import server.mechanic.game.GameSession;
import server.mechanic.game.GameUser;
import server.mechanic.services.GameSessionService;
import server.websocket.Message;
import server.websocket.RemotePointService;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Not thread safe! Per game mechanic service.
 */
@Service
public class ClientEventService {

    @NotNull
    private final RemotePointService remotePointService;

    private final Map<Integer, List<ClientEvent>> events = new ConcurrentHashMap<>();

    public void pushClientEvent(@NotNull Integer userId, @NotNull ClientEvent event) {
        this.events.putIfAbsent(userId, new ArrayList<>());
        final List<ClientEvent> clientSnaps = events.get(userId);
        clientSnaps.add(event);
    }

    public ClientEventService(@NotNull RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
    }

    @NotNull
    public List<ClientEvent> getEventForUser(@NotNull Integer userId) {
        return events.getOrDefault(userId, Collections.emptyList());
    }

    public void processEventsFor(@NotNull GameSession gameSession) {
        final Collection<GameUser> players = new ArrayList<>();
        players.add(gameSession.getFirst());
        players.add(gameSession.getSecond());
        for (GameUser player : players) {
            final List<ClientEvent> playerEvents = getEventForUser(player.getUserId());
            if (playerEvents.isEmpty()) {
                continue;
            }

            for (ClientEvent evt: playerEvents) {
                final Map<Integer, Message> messagesToUsers = evt.operate(gameSession, player);
                try {
                    for (Map.Entry<Integer, Message> messageToUser: messagesToUsers.entrySet()) {
                        remotePointService.sendMessageToUser(messageToUser.getKey(), messageToUser.getValue());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void clearForUser(Integer userProfileId) {
        events.remove(userProfileId);
    }

    public void resetGarbage(GameSessionService gameSessionService) {
        events.forEach((userId, event) -> {
                    if (!gameSessionService.isPlaying(userId)) {
                        events.remove(userId);
                    }
                }
        );
    }

    public void resetForGameSession(GameSession gameSession) {
        events.remove(gameSession.getFirst().getUserId());
        events.remove(gameSession.getSecond().getUserId());
    }
}
