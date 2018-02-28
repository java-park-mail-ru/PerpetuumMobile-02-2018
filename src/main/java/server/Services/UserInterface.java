package server.Services;

import server.Model.User;

public interface UserInterface {
    void addUser(User newUser);

    User getUserById(Integer id);

    Boolean isEmailRegistered(String email);

    Boolean isLoginRegistered(String login);

    Integer authorizeUserByEmail(User tryAuth);

}
