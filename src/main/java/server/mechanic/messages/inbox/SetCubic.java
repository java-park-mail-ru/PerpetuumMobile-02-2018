package server.mechanic.messages.inbox;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import server.mechanic.game.GameSession;
import server.mechanic.game.GameUser;
import server.mechanic.game.map.Cell;
import server.mechanic.messages.outbox.CubicNotSet;
import server.mechanic.messages.outbox.CubicSet;
import server.mechanic.services.event.client.ClientEvent;
import server.websocket.Message;

import java.util.*;

public class SetCubic extends Message implements ClientEvent {
    @JsonProperty(value = "x")
    private Integer coordX;
    @JsonProperty(value = "y")
    private Integer coordY;
    private String colour;

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

    @Override
    public Map<Integer, Message> operate(GameSession gameSession, GameUser player) {

        Optional<Cell> cellInMapOpt = gameSession.getGameMap().getCells().stream().filter(
            cell -> cell.getCoordX().equals(this.coordX) && cell.getCoordY().equals(this.coordY) && !cell.isFixed()
        ).findFirst();

        Map<Integer, Message> messages = new HashMap<>();

        cellInMapOpt.ifPresent(cell -> {
            if (cell.getWhoSetUserId() != null) {
                return;
            }
            if (cell.getColour().equals(this.colour)) {
                cell.setWhoSetUserId(player.getUserId());
                final GameUser playerOpponent = gameSession.getEnemy(player.getUserId());
                player.setScore(player.getScore() + 1);

                final CubicSet messageSelf = new CubicSet(
                        this.coordX,
                        this.coordY,
                        this.colour,
                        true,
                        player.getScore(),
                        playerOpponent.getScore());
                final CubicSet messageOpponent = new CubicSet(
                        this.coordX,
                        this.coordY,
                        this.colour,
                        false,
                        playerOpponent.getScore(),
                        player.getScore());

                messages.put(player.getUserId(), messageSelf);
                messages.put(playerOpponent.getUserId(), messageOpponent);
                return;
            }
            final CubicNotSet messageSelf = new CubicNotSet(this.colour);
            messages.put(player.getUserId(), messageSelf);
        });

        return messages;
    }
}
