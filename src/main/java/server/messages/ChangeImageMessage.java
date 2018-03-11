package server.messages;

public class ChangeImageMessage {
    private String statusMessage;
    private String fileName;

    public ChangeImageMessage(String statusMessage, String fileName) {
        this.statusMessage = statusMessage;
        this.fileName = fileName;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
