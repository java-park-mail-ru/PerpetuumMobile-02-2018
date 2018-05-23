package server.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import server.model.User;
import server.model.UserAuth;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void getAllUsers() {
        User user1 = new User("testlogin", "testemail@mail.ru", "password");
        User user2 = new User("testlogin1", "testemail1@mail.ru", "password1");
        userService.addUser(user1);
        userService.addUser(user2);
        List<User> users;
        users = userService.getAllUsers();
        User settedUser1 = users.get(users.size()-2);
        User settedUser2 = users.get(users.size()-1);
        assertEquals(settedUser1.getLogin(), user1.getLogin());
        assertEquals(settedUser1.getEmail(), user1.getEmail());
        assertEquals(settedUser2.getLogin(), user2.getLogin());
        assertEquals(settedUser2.getEmail(), user2.getEmail());
    }

    @Test
    void addUser() {
        User newUser = new User("testname", "testemail@mail.ru", "testpassword");
        Integer id = userService.addUser(newUser);
        assertTrue(id > 0);
    }

    @Test
    void getUserById() {
        User newUser = new User("testname", "testemail@mail.ru", "testpassword");
        Integer id = userService.addUser(newUser);
        User settedUser = userService.getUserById(id);
        assertEquals(newUser.getLogin(), settedUser.getLogin());
        assertEquals(newUser.getEmail(), settedUser.getEmail());
    }

    @Test
    void isEmailRegistered() {
        User newUser = new User("testname", "testemail@mail.ru", "testpassword");
        Integer id = userService.addUser(newUser);
        User settedUser = userService.getUserById(id);
        assertTrue(userService.isEmailRegistered(settedUser.getEmail()));
    }

    @Test
    void isLoginRegistered() {
        User newUser = new User("testname", "testemail@mail.ru", "testpassword");
        Integer id = userService.addUser(newUser);
        User settedUser = userService.getUserById(id);
        assertTrue(userService.isLoginRegistered(settedUser.getLogin()));
    }

    @Test
    void authorizeUser() {
        User newUser = new User("testname", "testemail@mail.ru", "testpassword");
        UserAuth newUserAuth = new UserAuth("testname", "testpassword");
        Integer idNewUser = userService.addUser(newUser);
        Integer idFromAuthorization = userService.authorizeUser(newUserAuth);
        assertEquals(idNewUser, idFromAuthorization);
    }

    @Test
    void checkUserById() {
        User newUser = new User("testname", "testemail@mail.ru", "testpassword");
        Integer idNewUser = userService.addUser(newUser);
        assertTrue(userService.checkUserById(idNewUser));
    }
}