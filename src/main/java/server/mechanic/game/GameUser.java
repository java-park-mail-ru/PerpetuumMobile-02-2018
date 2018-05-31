package server.mechanic.game;

import server.model.User;
import javax.validation.constraints.NotNull;


public class GameUser {
    @NotNull
    private final User userProfile;
    private Integer score = 0;


    public GameUser(@NotNull User userProfile) {
        this.userProfile = userProfile;
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
}
