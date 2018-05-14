package server.mechanic.messages.outbox;

public class EndGame {
    private Integer your;
    private Integer opponent;
    private String result;

    public Integer getYour() {
        return your;
    }

    public void setYour(Integer your) {
        this.your = your;
    }

    public Integer getOpponent() {
        return opponent;
    }

    public void setOpponent(Integer opponent) {
        this.opponent = opponent;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
