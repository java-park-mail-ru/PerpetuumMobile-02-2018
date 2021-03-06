package server.websocket;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import server.mechanic.messages.inbox.CloseGame;
import server.mechanic.messages.inbox.JoinGame;
import server.mechanic.messages.inbox.SetCubic;
import server.mechanic.messages.outbox.CubicNotSet;
import server.mechanic.messages.outbox.CubicSet;
import server.mechanic.messages.outbox.EndGame;
import server.mechanic.messages.outbox.InitGame;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = JoinGame.Request.class, name = "READY"),
        @Type(value = InitGame.Request.class, name = "START_GAME"),
        @Type(value = SetCubic.class, name = "SET_CUBIC"),
        @Type(value = CubicSet.class, name = "CUBIC_SET"),
        @Type(value = CubicNotSet.class, name = "CUBIC_DROP"),
        @Type(value = CloseGame.class, name = "CLOSE"),
        @Type(value = EndGame.class, name = "END_GAME"),
})
public abstract class Message {
}