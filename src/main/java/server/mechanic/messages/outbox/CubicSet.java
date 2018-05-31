package server.mechanic.messages.outbox;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import server.websocket.Message;

public class CubicSet extends Message {
    @JsonProperty(value = "x")
    private Integer coordX;
    @JsonProperty(value = "y")
    private Integer coordY;
    private String colour;
    private Boolean youSet;
    private Integer your;
    private Integer opponent;

    public CubicSet() {
    }

    public CubicSet(Integer coordX, Integer coordY, String colour, Boolean youSet, Integer your, Integer opponent) {
        this.coordX = coordX;
        this.coordY = coordY;
        this.colour = colour;
        this.youSet = youSet;
        this.your = your;
        this.opponent = opponent;
    }

    @JsonGetter(value = "x")
    public Integer getCoordX() {
        return coordX;
    }

    @JsonSetter(value = "x")
    public void setCoordX(Integer coordX) {
        this.coordX = coordX;
    }

    @JsonGetter(value = "y")
    public Integer getCoordY() {
        return coordY;
    }

    @JsonSetter(value = "y")
    public void setCoordY(Integer coordY) {
        this.coordY = coordY;
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
