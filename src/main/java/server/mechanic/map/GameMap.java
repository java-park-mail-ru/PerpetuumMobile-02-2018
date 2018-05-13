package server.mechanic.map;


import java.util.ArrayList;
import java.util.List;

public class GameMap {

    private Integer countX;
    private Integer countY;
    private List<Cell> cells;
    private List<Cell> pool;


    public GameMap() {
        this.countX = 3;
        this.countY = 1;
        Cell cell1 = new Cell();
        cell1.setX(0);
        cell1.setY(0);
        cell1.setColour("#730d13");
        this.cells = new ArrayList<>();
        this.cells.add(cell1);
        Cell cell2 = new Cell();
        cell2.setX(1);
        cell2.setY(0);
        this.cells.add(cell2);
        Cell cell3 = new Cell();
        cell3.setX(2);
        cell3.setY(0);
        cell3.setColour("#dcd3e0");
        this.cells.add(cell3);
        Cell cell4 = new Cell();
        cell4.setColour("#f05a69");
        this.pool = new ArrayList<>();
        this.pool.add(cell4);

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
