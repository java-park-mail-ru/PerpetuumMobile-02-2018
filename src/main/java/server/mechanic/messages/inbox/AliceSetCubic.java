package server.mechanic.messages.inbox;

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
    private Integer place;
    private Integer cubicId;


    public Integer getCubicId() {
        return cubicId;
    }

    public void setCubicId(Integer cubicId) {
        this.cubicId = cubicId;
    }

    @Override
    public Map<Integer, Message> operate(GameSession gameSession, GameUser player) {

        Optional<Cell> cellInMapOpt = gameSession.getGameMap().getCells().stream().filter(
                cell -> cell.getPlace().equals(this.place) && !cell.isFixed()
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
                final Integer coordX = cell.getCoordX();
                final Integer coordY = cell.getCoordY();
                final CubicSet messageSelf = new CubicSet(
                        coordX,
                        coordY,
                        colour,
                        true,
                        player.getScore(),
                        playerOpponent.getScore());
                final CubicSet messageOpponent = new CubicSet(
                        coordX,
                        coordY,
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

    public Integer getPlace() {
        return place;
    }

    public void setPlace(Integer place) {
        this.place = place;
    }
}