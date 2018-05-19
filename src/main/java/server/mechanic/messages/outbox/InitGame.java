package server.mechanic.messages.outbox;

import server.mechanic.game.map.GameMap;
import server.model.User;
import server.websocket.Message;

public class InitGame {
    public static final class Request extends Message {
        private User opponent;
        private GameMap.GameMapClient map;

        public User getOpponent() {
            return opponent;
        }

        public void setOpponent(User opponent) {
            this.opponent = opponent;
        }

        public GameMap.GameMapClient getMap() {
            return map;
        }

        public void setMap(GameMap.GameMapClient map) {
            this.map = map;
        }
    }

}
