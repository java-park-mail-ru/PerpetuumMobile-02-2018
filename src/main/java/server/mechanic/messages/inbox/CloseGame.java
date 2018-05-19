package server.mechanic.messages.inbox;

import server.mechanic.game.GameSession;
import server.mechanic.game.GameUser;
import server.mechanic.services.event.client.ClientEvent;
import server.websocket.Message;

import java.util.*;

public class CloseGame extends Message implements ClientEvent {
    @Override
    public Map<Integer, Message> operate(GameSession gameSession, GameUser player) {
        Map<Integer, Message> messages = new HashMap<>();
        return  messages;
    }
}
