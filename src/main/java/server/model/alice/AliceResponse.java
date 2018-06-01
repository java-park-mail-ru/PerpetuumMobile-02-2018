package server.model.alice;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

public class AliceResponse {
    private String text;
    private String tts;
    private List<AliceButton> buttons;

    @JsonProperty(value = "end_session")
    private Boolean endSession;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTts() {
        return tts;
    }

    public void setTts(String tts) {
        this.tts = tts;
    }

    public List<AliceButton> getButtons() {
        return buttons;
    }

    public void setButtons(List<AliceButton> buttons) {
        this.buttons = buttons;
    }

    @JsonGetter(value = "end_session")
    public Boolean getEndSession() {
        return endSession;
    }

    @JsonSetter(value = "end_session")
    public void setEndSession(Boolean endSession) {
        this.endSession = endSession;
    }
}
