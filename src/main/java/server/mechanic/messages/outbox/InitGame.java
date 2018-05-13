package server.mechanic.messages.outbox;

import server.mechanic.map.GameMap;
import server.model.User;
import server.websocket.Message;

import javax.validation.constraints.NotNull;

public class InitGame {
    public static final class Request extends Message {
        private User opponent;
        private GameMap map;

        public User getOpponent() {
            return opponent;
        }

        public void setOpponent(User opponent) {
            this.opponent = opponent;
        }

        public GameMap getMap() {
            return map;
        }

        public void setMap(GameMap map) {
            this.map = map;
        }
    }

}
