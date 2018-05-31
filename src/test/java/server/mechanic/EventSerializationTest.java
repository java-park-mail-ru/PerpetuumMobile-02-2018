package server.mechanic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.mechanic.game.GameUser;
import server.mechanic.messages.inbox.SetCubic;
import server.mechanic.messages.outbox.CubicSet;
import server.model.User;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class EventSerializationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private GameUser player1;
    private GameUser player2;
    private User user1;
    private User user2;


    @BeforeEach
    void setUp() {
        user1 = new User(41,"user1", "user1@email.com", "12345", 0);
        user2 = new User(42,"user2", "user2@email.com", "12345", 0);
        player1 = new GameUser(user1);
        player2 = new GameUser(user2);
    }

    @Test
    void setCubicEvent() throws IOException {
        final String setCubicStr = "{\"x\":2,\"y\":0,\"colour\":\"#78f0c3\",\"type\":\"SET_CUBIC\"}";
        final SetCubic setCubic = objectMapper.readValue(setCubicStr, SetCubic.class);
        final String setCubicJSON = objectMapper.writeValueAsString(setCubic);
        assertNotNull(setCubicJSON);
    }

}