package server.mechanic.game.map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class GameMap {

    private Integer countX;
    private Integer countY;
    private List<Cell> cells;

    public GameMap() {    }

    public GameMapClient safeGet() {
        return new GameMapClient(this);
    }

    public static class GameMapClient {
        private Integer countX;
        private Integer countY;
        private List<Cell.CellClient> cells;
        private List<Cell.CellClient> pool;

        GameMapClient(GameMap gameMap) {
            this.countX = gameMap.getCountX();
            this.countY = gameMap.getCountY();
            this.pool = new ArrayList<>();
            gameMap.getCells().stream().filter(cell -> !cell.isFixed()).forEach(cell -> this.pool.add(cell.safeGetPool()));
            this.cells = new ArrayList<>();
            gameMap.getCells().forEach(cell -> this.cells.add(cell.safeGet()));
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

        public List<Cell.CellClient> getCells() {
            return cells;
        }

        public void setCells(List<Cell.CellClient> cells) {
            this.cells = cells;
        }

        public List<Cell.CellClient> getPool() {
            return pool;
        }

        public void setPool(List<Cell.CellClient> pool) {
            this.pool = pool;
        }
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
}
