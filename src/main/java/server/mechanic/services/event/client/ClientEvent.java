package server.mechanic.services.event.client;

import server.mechanic.game.GameSession;
import server.mechanic.game.GameUser;
import server.websocket.Message;

import java.util.Map;

public interface ClientEvent {
    Map<Integer, Message> operate(GameSession gameSession, GameUser player);
}
