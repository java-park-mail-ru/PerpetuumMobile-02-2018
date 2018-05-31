package server.mechanic.messages.handlers;

import org.springframework.stereotype.Component;
import server.mechanic.GameMechanics;
import server.mechanic.messages.inbox.SetCubic;
import server.websocket.MessageHandler;
import server.websocket.MessageHandlerContainer;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Component
public class SetCubicHandler extends MessageHandler<SetCubic> {
    @NotNull
    private final GameMechanics gameMechanics;
    @NotNull
    private final MessageHandlerContainer messageHandlerContainer;

    public SetCubicHandler(@NotNull GameMechanics gameMechanics, @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(SetCubic.class);
        this.gameMechanics = gameMechanics;
        this.messageHandlerContainer = messageHandlerContainer;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(SetCubic.class, this);
    }

    @Override
    public void handle(@NotNull SetCubic message, @NotNull Integer forUser) {
        gameMechanics.addClientEvent(forUser, message);
    }
}
