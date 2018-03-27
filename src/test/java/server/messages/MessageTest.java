package server.messages;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    private Message msg;

    @BeforeEach
    void setUp() {
        msg = new Message("testMsg");
    }

    @Test
    void getMessageTest() {
            assertEquals("testMsg", msg.getMessage());
    }

    @Test
    void setMessageTest() {
        msg.setMessage("testMsg2");
        assertEquals("testMsg2", msg.getMessage());
    }
}