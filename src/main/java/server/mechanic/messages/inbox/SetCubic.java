package server.mechanic.messages.inbox;

import server.mechanic.game.GameSession;
import server.mechanic.map.Coord;
import server.mechanic.messages.TestMessage;
import server.mechanic.services.event.client.ClientEvent;
import server.websocket.Message;
import server.websocket.RemotePointService;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class SetCubic extends Message implements ClientEvent {
    private Integer x;
    private Integer y;
    private String color;

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Message> operate(GameSession gameSession) {
        List<Message> messages = new ArrayList<>();
        TestMessage testMessage1 = new TestMessage("I receive SendCubic 1");
        TestMessage testMessage2 = new TestMessage("I receive SendCubic 2");
        messages.add(testMessage1);
        messages.add(testMessage2);
        return  messages;
    }
}
