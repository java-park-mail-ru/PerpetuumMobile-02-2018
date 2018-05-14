package server.mechanic.messages.inbox;

import server.mechanic.game.GameSession;
import server.mechanic.services.event.client.ClientEvent;
import server.websocket.Message;

import java.util.ArrayList;
import java.util.List;

public class CloseGame extends Message implements ClientEvent {
    public List<Message> operate(GameSession gameSession) {
        List<Message> messages = new ArrayList<>();
        return  messages;
    }
}
