package server.websocket;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import server.mechanic.messages.TestMessage;
import server.mechanic.messages.inbox.JoinGame;
import server.mechanic.messages.outbox.InitGame;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = JoinGame.Request.class, name = "READY"),
        @Type(value = InitGame.Request.class, name = "START_GAME"),
        @Type(TestMessage.class),
})
public abstract class Message {
}