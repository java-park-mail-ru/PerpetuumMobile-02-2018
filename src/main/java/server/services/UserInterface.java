package server.services;

import org.springframework.stereotype.Service;
import server.model.User;


public interface UserInterface {
    void addUser(User newUser);

    User getUserById(Integer id);

    Boolean isEmailRegistered(String email);

    Boolean isLoginRegistered(String login);

    Integer authorizeUserByEmail(User tryAuth);

}
