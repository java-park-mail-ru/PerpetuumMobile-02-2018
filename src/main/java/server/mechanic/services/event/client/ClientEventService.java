package server.mechanic.services.event.client;

import org.springframework.stereotype.Service;
import server.mechanic.game.GameSession;
import server.mechanic.game.GameUser;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Not thread safe! Per game mechanic service.
 */
@Service
public class ClientEventService {

    private final Map<Integer, List<ClientEvent>> events = new HashMap<>();

    public void pushClientEvent(@NotNull Integer userId, @NotNull ClientEvent event) {
        this.events.putIfAbsent(userId, new ArrayList<>());
        final List<ClientEvent> clientSnaps = events.get(userId);
        clientSnaps.add(event);
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

            for (ClientEvent evt: playerEvents){
                evt.operate(gameSession);
            }
        }
    }

    public void clearForUser(Integer userProfileId) {
        events.remove(userProfileId);
    }

    public void reset() {
        events.clear();
    }
}
