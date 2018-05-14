package server.mechanic.messages.inbox;

import server.mechanic.game.GameSession;
import server.mechanic.messages.outbox.CubicSet;
import server.mechanic.services.event.client.ClientEvent;
import server.websocket.Message;

import java.util.ArrayList;
import java.util.List;

public class SetCubic extends Message implements ClientEvent {
    private Integer x;
    private Integer y;
    private String colour;

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

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public List<Message> operate(GameSession gameSession) {
        System.out.println(this.colour);
        List<Message> messages = new ArrayList<>();
        CubicSet message1 = new CubicSet(2, 1, "#f05a69", true, 2,1);

//        TestMessage testMessage1 = new TestMessage("I receive SendCubic 1");
//        TestMessage testMessage2 = new TestMessage("I receive SendCubic 2");
        messages.add(message1);
        messages.add(message1);
        return  messages;
    }
}
