package server.mechanic.messages.inbox;

import server.mechanic.game.GameSession;
import server.mechanic.game.GameUser;
import server.mechanic.game.map.Cell;
import server.mechanic.messages.outbox.CubicNotSet;
import server.mechanic.messages.outbox.CubicSet;
import server.mechanic.services.event.client.ClientEvent;
import server.websocket.Message;

import java.util.*;

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

    @Override
    public Map<Integer, Message> operate(GameSession gameSession, GameUser player) {

        Optional<Cell> cellInMapOpt = gameSession.getGameMap().getCells().stream().filter(
                cell -> cell.getX().equals(this.x) && cell.getY().equals(this.y) && !cell.isFixed()
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

                final CubicSet messageSelf = new CubicSet(this.x, this.y, this.colour, true, player.getScore(), playerOpponent.getScore());
                final CubicSet messageOpponent = new CubicSet(this.x, this.y, this.colour, false, playerOpponent.getScore(), player.getScore());

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
