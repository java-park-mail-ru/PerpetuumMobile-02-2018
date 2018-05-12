package server.mechanic.messages.handlers;

import org.springframework.stereotype.Component;
import server.mechanic.GameMechanics;
import server.mechanic.messages.inbox.JoinGame;
import server.websocket.MessageHandler;
import server.websocket.MessageHandlerContainer;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;


@Component
public class JoinGameHandler extends MessageHandler<JoinGame.Request> {
    @NotNull
    private final GameMechanics gameMechanics;
    @NotNull
    private final MessageHandlerContainer messageHandlerContainer;

    public JoinGameHandler(@NotNull GameMechanics gameMechanics, @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(JoinGame.Request.class);
        this.gameMechanics = gameMechanics;
        this.messageHandlerContainer = messageHandlerContainer;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(JoinGame.Request.class, this);
    }

    @Override
    public void handle(@NotNull JoinGame.Request message, @NotNull Integer forUser) {
        gameMechanics.addUser(forUser);
    }
}
