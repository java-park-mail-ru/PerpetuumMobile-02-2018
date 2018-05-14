package server.mechanic.game;


import server.model.User;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class GameUser extends GameObject {
    @NotNull
    private final User userProfile;
    private Integer score = 0;


    public GameUser(@NotNull User userProfile) {//, @NotNull MechanicsTimeService timeService) {
        this.userProfile = userProfile;
//        addPart(MousePart.class, new MousePart());
//        addPart(MechanicPart.class, new MechanicPart(timeService));
    }

    @NotNull
    public User getUserProfile() {
        return userProfile;
    }

    @NotNull
    public Integer getUserId() {
        return userProfile.getId();
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
//    @Override
//    public @NotNull ServerPlayerSnap getSnap() {
//        return ServerPlayerSnap.snapPlayer(this);
//    }

//    public static class ServerPlayerSnap implements Snap<GameUser> {
//        private Id<UserProfile> userId;
//
//        private Map<String, Snap<? extends GamePart>> gameParts;
//
//        public Id<UserProfile> getUserId() {
//            return userId;
//        }
//
//        public Map<String, Snap<? extends GamePart>> getGameParts() {
//            return gameParts;
//        }
//
//        public void setUserId(Id<UserProfile> userId) {
//            this.userId = userId;
//        }
//
//        @NotNull
//        public static ServerPlayerSnap snapPlayer(@NotNull GameUser gameUser) {
//            final ServerPlayerSnap serverPlayerSnap = new ServerPlayerSnap();
//            serverPlayerSnap.userId = gameUser.getUserProfile().getId();
//            serverPlayerSnap.gameParts = new HashMap<>();
//            gameUser.getPartSnaps().forEach(part -> serverPlayerSnap.gameParts.put(part.getClass().getSimpleName(), part));
//            return serverPlayerSnap;
//        }
//    }

//    @Override
//    public String toString() {
//        return "GameUser{"
//                + "userProfile=" + userProfile
//                + '}';
//    }
}
