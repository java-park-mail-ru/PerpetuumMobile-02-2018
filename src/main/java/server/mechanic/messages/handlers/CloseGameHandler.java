package server.mechanic.messages.handlers;

import org.springframework.stereotype.Component;
import server.mechanic.GameMechanics;
import server.mechanic.messages.inbox.CloseGame;
import server.mechanic.messages.inbox.JoinGame;
import server.websocket.MessageHandler;
import server.websocket.MessageHandlerContainer;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Component
public class CloseGameHandler extends MessageHandler<CloseGame> {
    @NotNull
    private final GameMechanics gameMechanics;
    @NotNull
    private final MessageHandlerContainer messageHandlerContainer;

    public CloseGameHandler(@NotNull GameMechanics gameMechanics, @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(CloseGame.class);
        this.gameMechanics = gameMechanics;
        this.messageHandlerContainer = messageHandlerContainer;
    }
    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(CloseGame.class, this);
    }

    @Override
    public void handle(@NotNull CloseGame message, @NotNull Integer forUser) {
        gameMechanics.removeUser(forUser);
    }

}
