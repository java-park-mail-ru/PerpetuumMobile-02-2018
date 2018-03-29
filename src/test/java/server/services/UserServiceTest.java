package server.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void getAllUsers() {
    }

    @Test
    void addUser() {
    }

    @Test
    void getUserById() {
    }

    @Test
    void isEmailRegistered() {
    }

    @Test
    void isLoginRegistered() {
    }

    @Test
    void authorizeUser() {
    }

    @Test
    void checkUserById() {
    }
}