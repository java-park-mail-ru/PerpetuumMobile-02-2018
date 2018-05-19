package server.mechanic.messages.outbox;

import server.websocket.Message;

public class EndGame extends Message {
    private Integer your;
    private Integer opponent;
    private String result;
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getYour() {
        return your;
    }

    public void setYour(Integer your) {
        this.your = your;
    }

    public Integer getOpponent() {
        return opponent;
    }

    public void setOpponent(Integer opponent) {
        this.opponent = opponent;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
