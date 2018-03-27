package server.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;


import static org.junit.jupiter.api.Assertions.*;

class ChangeUserTest {

    private ChangeUser player;

    @BeforeEach
    void setUp() {
        player = new ChangeUser();
    }

    @Test
    void OldPasswordTest() {
        player.setOldPassword("1234");
        assertEquals("1234", player.getOldPassword());
    }

    @Test
    void ImageTest() {
        player.setImage("avatar.png");
        assertEquals("avatar.png", player.getImage());
    }


    @Test
    void NewPasswordTest() {
        player.setNewPassword("4321");
        assertEquals("4321", player.getNewPassword());
    }

}