package server.model.alice;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class AliceMeta {
    private String locale;
    private String timezone;
    @JsonProperty(value = "client_id")
    private String clientId;

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @JsonGetter(value = "client_id")
    public String getClientId() {
        return clientId;
    }

    @JsonSetter(value = "client_id")
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
