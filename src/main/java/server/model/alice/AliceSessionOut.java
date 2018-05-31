package server.model.alice;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class AliceSessionOut {
    @JsonProperty(value = "session_id")
    private String sessionId;

    @JsonProperty(value = "message_id")
    private Integer messageId;

    @JsonProperty(value = "user_id")
    private String userId;

    AliceSessionOut() {

    }

    public AliceSessionOut(AliceSession aliceSession) {
        this.sessionId = aliceSession.getSessionId();
        this.messageId = aliceSession.getMessageId();
        this.userId = aliceSession.getUserId();
    }

    @JsonGetter(value = "session_id")
    public String getSessionId() {
        return sessionId;
    }

    @JsonSetter(value = "session_id")
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @JsonGetter(value = "message_id")
    public Integer getMessageId() {
        return messageId;
    }

    @JsonSetter(value = "message_id")
    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    @JsonGetter(value = "user_id")
    public String getUserId() {
        return userId;
    }

    @JsonSetter(value = "user_id")
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
