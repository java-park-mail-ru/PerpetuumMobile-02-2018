package server.mechanic.messages.outbox;

import server.websocket.Message;

public class CubicSet extends Message {
    private Integer x;
    private Integer y;
    private String colour;
    private Boolean youSet;
    private Integer your;
    private Integer opponent;

    public CubicSet() {
    }

    public CubicSet(Integer x, Integer y, String colour, Boolean youSet, Integer your, Integer opponent) {
        this.x = x;
        this.y = y;
        this.colour = colour;
        this.youSet = youSet;
        this.your = your;
        this.opponent = opponent;
    }

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

    public Boolean getYouSet() {
        return youSet;
    }

    public void setYouSet(Boolean youSet) {
        this.youSet = youSet;
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
}
