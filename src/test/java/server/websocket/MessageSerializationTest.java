package server.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import server.mechanic.messages.inbox.CloseGame;
import server.mechanic.messages.inbox.JoinGame;
import server.mechanic.messages.inbox.SetCubic;
import server.mechanic.messages.outbox.CubicNotSet;
import server.mechanic.messages.outbox.CubicSet;
import server.mechanic.messages.outbox.EndGame;
import server.mechanic.messages.outbox.InitGame;
import server.model.User;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MessageSerializationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @ParameterizedTest
    @MethodSource("twoWaySerializableProvider")
    void test2WaySerializable(Message message) throws IOException {
        final String requestJson = objectMapper.writeValueAsString(message);
        final Message fromJson = objectMapper.readValue(requestJson, Message.class);
        assertEquals(message.getClass(), fromJson.getClass());
    }

    @ParameterizedTest
    @MethodSource("oneWaySerializableProvider")
    void test1WaySerializable(Message message) throws IOException {
        final String messageJson = objectMapper.writeValueAsString(message);
        assertNotNull(messageJson);
    }

    private static Stream<Message> twoWaySerializableProvider() {
        final JoinGame.Request joinRequest = new JoinGame.Request();
        final SetCubic setCubic = new SetCubic();
        setCubic.setColour("#11aacc");
        setCubic.setCoordX(1);
        setCubic.setCoordY(4);
        final CloseGame closeGame = new CloseGame();

        return Stream.of(joinRequest,
                setCubic,
                closeGame);

    }

    private static Stream<Message> oneWaySerializableProvider() {
        final InitGame.Request initGame = new InitGame.Request();
        initGame.setOpponent(new User());
        final CubicSet cubicSet = new CubicSet();
        cubicSet.setColour("#aabccb");
        cubicSet.setCoordX(2);
        cubicSet.setCoordY(5);
        cubicSet.setOpponent(42);
        cubicSet.setYour(41);
        cubicSet.setYouSet(true);
        final CubicNotSet cubicNotSet = new CubicNotSet();
        cubicNotSet.setColour("#ccaabb");
        final EndGame endGame = new EndGame();
        endGame.setResult("WIN");
        endGame.setReason("You win");
        endGame.setOpponent(42);
        endGame.setYour(41);

        return Stream.of(initGame,
                cubicSet,
                cubicNotSet,
                endGame);
    }
}