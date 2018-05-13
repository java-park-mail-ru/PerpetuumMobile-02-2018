package server.mechanic.services.event.client;

import server.mechanic.game.GameSession;

public interface ClientEvent {
    void operate(GameSession g);
}
