package server.mechanic.messages.handlers;

import org.springframework.stereotype.Component;
import server.mechanic.GameMechanics;
import server.mechanic.messages.inbox.AliceSetCubic;
import server.websocket.MessageHandler;
import server.websocket.MessageHandlerContainer;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Component
public class AliceSetCubicHandler extends MessageHandler<AliceSetCubic> {
    @NotNull
    private final GameMechanics gameMechanics;
    @NotNull
    private final MessageHandlerContainer messageHandlerContainer;

    public AliceSetCubicHandler(@NotNull GameMechanics gameMechanics, @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(AliceSetCubic.class);
        this.gameMechanics = gameMechanics;
        this.messageHandlerContainer = messageHandlerContainer;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(AliceSetCubic.class, this);
    }

    @Override
    public void handle(@NotNull AliceSetCubic message, @NotNull Integer forUser) {
        gameMechanics.addClientEvent(forUser, message);
    }
}
