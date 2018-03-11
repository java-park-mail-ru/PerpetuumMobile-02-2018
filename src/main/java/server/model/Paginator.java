package server.model;

public class Paginator<T> {
    private Integer maxPageNum;
    private T filling;

    public Paginator() {

    }

    public Paginator(Integer _maxPageNum, T _filling) {
        this.maxPageNum = _maxPageNum;
        this.filling = _filling;
    }

    public Integer getMaxPageNum() {
        return maxPageNum;
    }

    public void setMaxPageNum(Integer maxPageNum) {
        this.maxPageNum = maxPageNum;
    }

    public T getFilling() {
        return filling;
    }

    public void setFilling(T filling) {
        this.filling = filling;
    }
}
