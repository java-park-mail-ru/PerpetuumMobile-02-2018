package server.model.alice;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class AliceSession {
    @JsonProperty(value = "new")
    private Boolean newIndicator;
    @JsonProperty(value = "message_id")
    private Integer messageId;
    @JsonProperty(value = "session_id")
    private String sessionId;
    @JsonProperty(value = "skill_id")
    private String skillId;
    @JsonProperty(value = "user_id")
    private String userId;

    @JsonGetter(value = "new")
    public Boolean getNewIndicator() {
        return newIndicator;
    }

    @JsonSetter(value = "new")
    public void setNewIndicator(Boolean newIndicator) {
        this.newIndicator = newIndicator;
    }

    @JsonGetter(value = "message_id")
    public Integer getMessageId() {
        return messageId;
    }

    @JsonSetter(value = "message_id")
    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    @JsonGetter(value = "session_id")
    public String getSessionId() {
        return sessionId;
    }

    @JsonSetter(value = "session_id")
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @JsonGetter(value = "skill_id")
    public String getSkillId() {
        return skillId;
    }

    @JsonSetter(value = "skill_id")
    public void setSkillId(String skillId) {
        this.skillId = skillId;
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
