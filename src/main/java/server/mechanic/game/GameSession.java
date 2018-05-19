package server.mechanic.game;

import server.mechanic.game.map.GameMap;
import server.mechanic.services.GameSessionService;
import server.model.User;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class GameSession {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    @NotNull
    private final Integer sessionId;
    @NotNull
    private final GameUser first;
    @NotNull
    private final GameUser second;
    @NotNull
    private final GameMap gameMap;
    @NotNull
    private final GameSessionService gameSessionService;

    public GameSession(@NotNull User user1,
                       @NotNull User user2,
                       @NotNull GameMap gameMap,
                       @NotNull GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
        this.sessionId = ID_GENERATOR.getAndIncrement();
        this.first = new GameUser(user1);
        this.gameMap = gameMap;
        this.second = new GameUser(user2);
    }

    @NotNull
    public Integer getSessionId() {
        return sessionId;
    }

    @NotNull
    public GameUser getEnemy(@NotNull Integer userId) {
        if (userId.equals(first.getUserId())) {
            return second;
        }
        if (userId.equals(second.getUserId())) {
            return first;
        }
        throw new IllegalArgumentException("Requested enemy for game but user not participant");
    }

    @NotNull
    public GameUser getFirst() {
        return first;
    }

    @NotNull
    public GameUser getSecond() {
        return second;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    @Override
    public boolean equals(Object subj) {
        if (this == subj) {
            return true;
        }
        if (subj == null || getClass() != subj.getClass()) {
            return false;
        }

        final GameSession another = (GameSession) subj;

        return sessionId.equals(another.sessionId);
    }

    @NotNull
    public List<GameUser> getPlayers() {
        return Arrays.asList(first, second);
    }

    @Override
    public int hashCode() {
        return sessionId.hashCode();
    }


    public boolean tryFinishGameClose(Integer userId) {
        String reasonFirst;
        String reasonSecond;

        if (getFirst().getUserId().equals(userId)) {
            reasonFirst = "You close the game";
            reasonSecond = "Your opponent close the game";
        } else {
            reasonFirst = "Your opponent close the game";
            reasonSecond = "You close the game";
        }
        gameSessionService.finishGame(this, getEnemy(userId).getUserId(), reasonFirst, reasonSecond);
        return true;
    }

    public boolean tryFinishGame() {
        Boolean finishCond = this.gameMap.getCells().stream().allMatch(cell -> cell.isFixed() || cell.getWhoSetUserId() != null);
        if (!finishCond) {
            return false;
        }
        Integer winnerId;
        String reasonFirst;
        String reasonSecond;
        if (first.getScore() > second.getScore()) {
            winnerId = first.getUserId();
            reasonFirst = "You scored more points than your opponent";
            reasonSecond = "You scored less points than your opponent";
        } else if (second.getScore() > first.getScore()) {
            winnerId = second.getUserId();
            reasonFirst = "You scored less points than your opponent";
            reasonSecond = "You scored more points than your opponent";
        } else {
            winnerId = null;
            reasonFirst = "You scored the same points as your opponent";
            reasonSecond = reasonFirst;
        }
        gameSessionService.finishGame(this, winnerId, reasonFirst, reasonSecond);
        return true;
    }
}
