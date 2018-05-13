package server.mechanic.services.event.client;

import server.mechanic.game.GameSession;
import server.websocket.Message;

import java.util.List;

public interface ClientEvent {
    List<Message> operate(GameSession g);
}
