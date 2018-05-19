package server.mechanic.messages.outbox;

import server.websocket.Message;

public class CubicNotSet extends Message {
    private String colour;

    public CubicNotSet() {
    }

    public CubicNotSet(String colour) {
        this.colour = colour;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}
