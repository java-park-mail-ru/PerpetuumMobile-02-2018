package server.messages;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;


import static org.junit.jupiter.api.Assertions.*;

class ChangeImageMessageTest {
    private ChangeImageMessage msg;

    @BeforeEach
    void setUp() {
        msg = new ChangeImageMessage("ok", "test.jpg");
    }

    @Test
    void getStatusMessageTest() {
        assertEquals("ok", msg.getStatusMessage());
    }

    @Test
    void setStatusMessageTest() {
        msg.setStatusMessage("new");
        assertEquals("new", msg.getStatusMessage());
    }

    @Test
    void getFileNameTest() {
        assertEquals("test.jpg", msg.getFileName());
    }

    @Test
    void setFileNameTest() {
        msg.setFileName("new.jpg");
        assertEquals("new.jpg", msg.getFileName());
    }
}