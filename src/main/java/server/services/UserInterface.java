package server.services;

import server.model.User;


public interface UserInterface {
    Integer addUser(User newUser);

    User getUserById(Integer id);

    Boolean isEmailRegistered(String email);

    Boolean isLoginRegistered(String login);

    Integer authorizeUserByEmail(User tryAuth);

    String checkUserById(Integer userIdInDB);
}
