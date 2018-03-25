package server.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User player;

    @BeforeEach
    void setUp() {
        player = new User("player", "test@test.ru", "1234");
    }

    @Test
    void getScoreTest() {
        assertEquals(Integer.valueOf(0), player.getScore());
    }

    @Test
    void getLoginTest() {
        assertEquals("player", player.getLogin());
    }


    @Test
    void getPasswordTest() {
        assertEquals("1234", player.getPassword());
    }

    @Test
    void getEmailTest() {
        assertEquals("test@test.ru", player.getEmail());
    }

    @Test
    void getImageTest() {
        assertEquals("no_avatar.png", player.getImage());
    }

}