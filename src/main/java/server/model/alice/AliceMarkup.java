package server.model.alice;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class AliceMarkup {
    @JsonProperty(value = "dangerous_context")
    private Boolean dangerousContext;

    @JsonGetter(value = "dangerous_context")
    public Boolean getDangerousContext() {
        return dangerousContext;
    }

    @JsonSetter(value = "dangerous_context")
    public void setDangerousContext(Boolean dangerousContext) {
        this.dangerousContext = dangerousContext;
    }
}
