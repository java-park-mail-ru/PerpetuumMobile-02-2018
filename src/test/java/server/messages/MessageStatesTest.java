package server.messages;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class MessageStatesTest {

    @Test
    void getMessage() {
        assertEquals("successful authorize", MessageStates.AUTHORIZED.getMessage());
    }
}