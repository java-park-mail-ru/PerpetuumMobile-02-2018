package server.model;

public class SaveResult {
    private Integer levelNum;
    private Integer time;

    public SaveResult() { }

    public int getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public int getLevelNum() {
        return levelNum;
    }

    public void setLevelNum(Integer levelNum) {
        this.levelNum = levelNum;
    }

}
