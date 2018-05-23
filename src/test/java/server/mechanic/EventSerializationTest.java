package server.mechanic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.mechanic.game.GameUser;
import server.mechanic.game.map.Cell;
import server.mechanic.game.map.GameMap;
import server.mechanic.messages.inbox.SetCubic;
import server.mechanic.messages.outbox.CubicNotSet;
import server.mechanic.messages.outbox.CubicSet;
import server.mechanic.messages.outbox.EndGame;
import server.mechanic.messages.outbox.InitGame;
import server.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @Test
    void CubicSet() throws IOException {
        final CubicSet cubicSet = new CubicSet();
        cubicSet.setCoordX(5);
        cubicSet.setCoordY(5);
        cubicSet.setColour("#magicC");
        cubicSet.setYour(10);
        cubicSet.setOpponent(9);
        cubicSet.setYouSet(true);
        final String result = objectMapper.writeValueAsString(cubicSet);
        objectMapper.readValue(result, CubicSet.class);
    }

    @Test
    void CubicNotSet() throws IOException {
        final CubicNotSet cubicNotSet = new CubicNotSet();
        cubicNotSet.setColour("#magicC");
        final String result = objectMapper.writeValueAsString(cubicNotSet);
        objectMapper.readValue(result, CubicNotSet.class);
    }

    @Test
    void EndGame() throws IOException {
        final EndGame endGame = new EndGame();
        endGame.setResult("WIN");
        endGame.setReason("You win");
        endGame.setOpponent(42);
        endGame.setYour(41);
        final String result = objectMapper.writeValueAsString(endGame);
        objectMapper.readValue(result,EndGame.class);
    }

    @Test
    void serverInitTest() throws IOException {
        final InitGame.Request initGame = new InitGame.Request();
        final User opponent = new User();
        opponent.setLogin("MegaUser");
        opponent.setImage("opyat.jpeg");
        initGame.setOpponent(opponent);
        final GameMap gameMap = new GameMap();
        gameMap.setCountX(1);
        gameMap.setCountY(1);
        Cell cell = new Cell();
        cell.setColour("#magicC");
        cell.setFixed(false);
        List<Cell> cells = new ArrayList<>();
        cells.add(cell);
        gameMap.setCells(cells);
        final String initGameJson = objectMapper.writeValueAsString(initGame);
        assertNotNull(initGameJson);
    }

}