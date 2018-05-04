package server.websocket;

//import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
//import server.mechanic.messages.TestMessage;
import server.services.UserService;

import javax.validation.constraints.NotNull;
//import java.io.IOException;

@Component
public class GameSocketHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSocketHandler.class);
    private static final CloseStatus ACCESS_DENIED = new CloseStatus(4500, "Not logged in. Access denied");

    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final RemotePointService remotePointService;

    public GameSocketHandler(@NotNull UserService userService, @NotNull RemotePointService remotePointService, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.remotePointService = remotePointService;
        this.userService = userService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        //        final Integer userId = (Integer) webSocketSession.getAttributes().get("userId");
        //        if (userId == null || userService.getUserById(userId) == null) {
        //            LOGGER.warn("User requested websocket is not registered or not logged in. Opening websocket session is denied.");
        //            //closeSessionSilently(webSocketSession, ACCESS_DENIED);
        //            return;
        //        }
        Integer userId = 42;
        remotePointService.registerUser(userId, webSocketSession);
        //        TestMessage tMessage = new TestMessage("Hi. This message come over websocket!!!");
        //        try {
        //            remotePointService.sendMessageToUser(userId, tMessage);
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }
    }


    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) {
        LOGGER.warn("Websocket transport problem", throwable);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) {
        final Integer userId = (Integer) webSocketSession.getAttributes().get("userId");
        if (userId == null) {
            LOGGER.warn("User disconnected but his session was not found (closeStatus=" + closeStatus + ')');
            return;
        }
        remotePointService.removeUser(userId);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
