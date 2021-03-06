package server.dao;

import org.springframework.lang.Nullable;
import server.model.User;
import server.model.UserAuth;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface UserDao {

    @Nullable
    User getUserById(@NotNull Integer id);

    User getUserByEmail(@NotNull String email);

    User getUserByUsername(@NotNull String username);

    List<User> getAllUsers();


    Integer addUser(@NotNull User newUser);

    Boolean isEmailRegistered(@NotNull String email);

    Boolean isLoginRegistered(@NotNull String login);

    Integer authorizeUser(@NotNull UserAuth tryAuth);

    boolean checkUserById(@NotNull Integer userIdInDB);

    boolean updateUser(@NotNull User user);

    boolean updateUserPassword(User user);

    void increaseScoreById(@NotNull Integer userId, @NotNull Integer scoreIncrease);

}
