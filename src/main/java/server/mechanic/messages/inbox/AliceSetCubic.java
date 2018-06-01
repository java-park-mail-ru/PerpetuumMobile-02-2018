package server.mechanic.messages.inbox;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import server.mechanic.game.GameSession;
import server.mechanic.game.GameUser;
import server.mechanic.game.map.Cell;
import server.mechanic.messages.outbox.CubicSet;
import server.mechanic.services.event.client.ClientEvent;
import server.websocket.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AliceSetCubic extends Message implements ClientEvent {
    @JsonProperty(value = "x")
    private Integer coordX;
    @JsonProperty(value = "y")
    private Integer coordY;
    private Integer cubicId;

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

    public Integer getCubicId() {
        return cubicId;
    }

    public void setCubicId(Integer cubicId) {
        this.cubicId = cubicId;
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
            if (cell.getCubicId().equals(this.cubicId)) {
                cell.setWhoSetUserId(player.getUserId());
                final GameUser playerOpponent = gameSession.getEnemy(player.getUserId());
                player.setScore(player.getScore() + 1);
                final String colour = cell.getColour();
                final CubicSet messageSelf = new CubicSet(
                        this.coordX,
                        this.coordY,
                        colour,
                        true,
                        player.getScore(),
                        playerOpponent.getScore());
                final CubicSet messageOpponent = new CubicSet(
                        this.coordX,
                        this.coordY,
                        colour,
                        false,
                        playerOpponent.getScore(),
                        player.getScore());

                messages.put(player.getUserId(), messageSelf);
                messages.put(playerOpponent.getUserId(), messageOpponent);
                return;
            }
        });

        return messages;
    }
}