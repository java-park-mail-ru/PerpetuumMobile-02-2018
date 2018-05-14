package server.mechanic.map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GameMap {

    private Integer countX;
    private Integer countY;
    private List<Cell> cells;
    private List<Cell> pool;

    public GameMap() {    }

    @JsonIgnore
    public GameMap getMapForClient() {
        GameMap mapForUser = new GameMap();
        mapForUser.countX = this.countX;
        mapForUser.countY = this.countY;
        mapForUser.pool = this.pool;
        mapForUser.cells = this.cells;

        mapForUser.cells.forEach(cell -> {
           if (!cell.isFixed()) {
               cell.setColour(null);
           }
        });

        return mapForUser;
    }

    public Integer getCountX() {
        return countX;
    }

    public void setCountX(Integer countX) {
        this.countX = countX;
    }

    public Integer getCountY() {
        return countY;
    }

    public void setCountY(Integer countY) {
        this.countY = countY;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void setCells(List<Cell> cells) {
        this.cells = cells;
    }

    public List<Cell> getPool() {
        return pool;
    }

    public void setPool(List<Cell> pool) {
        this.pool = pool;
    }
}
