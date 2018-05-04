package server.mechanic.messages;

import server.websocket.Message;

public class TestMessage extends Message {
    private String text;

    public TestMessage() {
    }

    public TestMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
