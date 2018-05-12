package server.mechanic.game;


import server.mechanic.services.GameSessionService;
import server.model.User;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class GameSession {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
    private boolean isFinished;

    @NotNull
    private final Integer sessionId;
    @NotNull
    private final GameUser first;
    @NotNull
    private final GameUser second;
//    @NotNull
//    private final Board board;
    @NotNull
    private final GameSessionService gameSessionService;

    public GameSession(@NotNull User user1,
                       @NotNull User user2,
                       @NotNull GameSessionService gameSessionService) {
//                       @NotNull MechanicsTimeService mechanicsTimeService) {
        this.gameSessionService = gameSessionService;
        this.sessionId = ID_GENERATOR.getAndIncrement();
        this.first = new GameUser(user1);//, mechanicsTimeService);
        this.second = new GameUser(user2);//, mechanicsTimeService);
//        this.board = new Board(this, shuffler);
        this.isFinished = false;
    }

    @NotNull
    public Integer getSessionId() {
        return sessionId;
    }

//    @NotNull
//    public Board getBoard() {
//        return board;
//    }

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

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished() {
        isFinished = true;
    }

    public void terminateSession() {
        gameSessionService.forceTerminate(this, true);
    }

    @Override
    public int hashCode() {
        return sessionId.hashCode();
    }

//    @Override
//    public String toString() {
//        return '['
//                + "sessionId=" + sessionId
//                + ", first=" + first
//                + ", second=" + second
//                + ']';
//    }

//    public boolean tryFinishGame() {
//        if (first.claimPart(MechanicPart.class).getScore() >= Config.SCORES_TO_WIN
//                || second.claimPart(MechanicPart.class).getScore() >= Config.SCORES_TO_WIN) {
//            gameSessionService.finishGame(this);
//            isFinished = true;
//            return true;
//        }
//        return false;
//    }
}
