package server.model.alice;

public class AliceIn {

    private AliceMeta meta;
    private AliceRequest request;
    private AliceSession session;
    private String version;

    public AliceMeta getMeta() {
        return meta;
    }

    public void setMeta(AliceMeta meta) {
        this.meta = meta;
    }

    public AliceRequest getRequest() {
        return request;
    }

    public void setRequest(AliceRequest request) {
        this.request = request;
    }

    public AliceSession getSession() {
        return session;
    }

    public void setSession(AliceSession session) {
        this.session = session;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
