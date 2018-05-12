package server.mechanic;

import javax.validation.constraints.NotNull;

public interface GameMechanics {

//    void addClientSnapshot(@NotNull Id<UserProfile> userId, @NotNull ClientSnap clientSnap);

    void addUser(@NotNull Integer userId);

    void gmStep(long frameTime);

//    void reset();
}
