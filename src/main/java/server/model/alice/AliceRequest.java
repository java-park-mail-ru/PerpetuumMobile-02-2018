package server.model.alice;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class AliceRequest {
    private String command;
    @JsonProperty(value = "original_utterance")
    private String originalUtterance;
    private String type;
    private AliceMarkup markup;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @JsonGetter(value = "original_utterance")
    public String getOriginalUtterance() {
        return originalUtterance;
    }

    @JsonSetter(value = "original_utterance")
    public void setOriginalUtterance(String originalUtterance) {
        this.originalUtterance = originalUtterance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AliceMarkup getMarkup() {
        return markup;
    }

    public void setMarkup(AliceMarkup markup) {
        this.markup = markup;
    }
}
