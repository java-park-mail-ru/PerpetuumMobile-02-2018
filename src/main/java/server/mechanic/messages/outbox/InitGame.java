package server.mechanic.messages.outbox;

import server.websocket.Message;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class InitGame {
    public static final class Request extends Message {
        private Integer self;
        private Integer opponent;
//        private Board.BoardSnap board;
//        private Map<Integer, GameUser.ServerPlayerSnap> players;
        private Map<Integer, String> names;
//        private Map<Integer, String> colors;

//        public Board.BoardSnap getBoard() {
//            return board;
//        }

//        public void setBoard(Board.BoardSnap board) {
//            this.board = board;
//        }

        @NotNull
        public Map<Integer, String> getNames() {
            return names;
        }

        public void setNames(@NotNull Map<Integer, String> names) {
            this.names = names;
        }

        @NotNull
        public Integer getSelf() {
            return self;
        }

        public Integer getOpponent() {
            return opponent;
        }

        public void setOpponent(Integer opponent) {
            this.opponent = opponent;
        }

//        @NotNull
//        public Map<Integer, String> getColors() {
//            return colors;
//        }

//        public void setColors(@NotNull Map<Integer, String> colors) {
//            this.colors = colors;
//        }

        public void setSelf(@NotNull Integer self) {
            this.self = self;
        }

//        @NotNull
//        public Map<Integer, GameUser.ServerPlayerSnap> getPlayers() {
//            return players;
//        }

//        public void setPlayers(@NotNull Map<Id<UserProfile>, GameUser.ServerPlayerSnap> players) {
//            this.players = players;
//        }
    }

}
