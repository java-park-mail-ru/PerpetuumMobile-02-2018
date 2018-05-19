package server.mechanic.services.event.client;

import server.mechanic.game.GameSession;
import server.mechanic.game.GameUser;
import server.websocket.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ClientEvent {
    Map<Integer, Message> operate(GameSession g, GameUser player);
}
