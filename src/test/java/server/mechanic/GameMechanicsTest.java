package server.mechanic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.lang.Nullable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import server.mechanic.game.GameSession;
import server.mechanic.services.GameSessionService;
import server.model.User;
import server.services.UserService;
import server.websocket.RemotePointService;

import javax.validation.constraints.NotNull;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

//@Transactional
@SuppressWarnings({"MagicNumber", "NullableProblems", "SpringJavaAutowiredMembersInspection"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith(SpringExtension.class)
class GameMechanicsTest {
    @MockBean
    private RemotePointService remotePointService;
    @MockBean
    private UserService userService;
    @MockBean // purposely replace real executor
    private MechanicsExecutor mechanicsExecutor;
    @Autowired
    private GameMechanics gameMechanics;
    @Autowired
    private GameSessionService gameSessionService;
    @NotNull
    private User user1;
    @NotNull
    private User user2;

    @BeforeEach
    void setUp() {
        when(remotePointService.isConnected(anyInt())).thenReturn(true);
        when(userService.checkUserById(anyInt())).thenReturn(true);
        user1 = new User(41,"Jhon", "jhon@mail.ru", "12345", 0);
        user2 = new User(42,"Vasya", "vasya@mail.ru", "12345", 0);
        when(userService.getUserById(41)).thenReturn(user1);
        when(userService.getUserById(42)).thenReturn(user2);
    }

    @AfterEach
    void tearDown() {
        gameSessionService.getSessions().forEach(session -> gameSessionService.forceTerminate(session, false));
    }

    @Test
    void gameStartedTest() {
        startGame(user1.getId(), user2.getId());
    }

    @NotNull
    private GameSession startGame(@NotNull Integer player1, @NotNull Integer player2) {
        gameMechanics.addUser(player1);
        gameMechanics.addUser(player2);
        gameMechanics.gmStep(1);
        final GameSession gameSession = gameSessionService.getSessionForUser(player1);
        assertNotNull(gameSession, "Game session should be started on closest tick, but it didn't");
        return gameSession;
    }

}