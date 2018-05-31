package server.model.alice;

public class AliceOut {
    private AliceResponse response;
    private AliceSessionOut session;
    private String version;

    public AliceResponse getResponse() {
        return response;
    }

    public void setResponse(AliceResponse response) {
        this.response = response;
    }

    public AliceSessionOut getSession() {
        return session;
    }

    public void setSession(AliceSessionOut session) {
        this.session = session;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
