package server.services;

import server.model.User;

import java.util.List;


public interface UserInterface {
    List<User> getAllUsers();

    Integer addUser(User newUser);

    User getUserById(Integer id);

    Boolean isEmailRegistered(String email);

    Boolean isLoginRegistered(String login);

    Integer authorizeUser(User tryAuth);

    User checkUserById(Integer userIdInDB);
}
