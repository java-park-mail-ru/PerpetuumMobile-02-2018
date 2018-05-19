package server.mechanic.game.map;

public class Cell {
    private Integer x;
    private Integer y;
    private String colour;
    private Boolean fixed;
    private Integer whoSetUserId;


    public Boolean isFixed() {
        return fixed;
    }

    public void setFixed(Boolean fixed) {
        this.fixed = fixed;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }


    public CellClient safeGet() {
        final CellClient cell = new CellClient(this);
        if (!cell.fixed) {
            cell.colour = null;
        }
        return cell;
    }

    public CellClient safeGetPool() {
        final CellClient cell = new CellClient(this);
        if (cell.fixed) {
            return null;
        }
        cell.x = null;
        cell.y = null;
        return cell;
    }

    public Integer getWhoSetUserId() {
        return whoSetUserId;
    }

    public void setWhoSetUserId(Integer whoSetUserId) {
        this.whoSetUserId = whoSetUserId;
    }

    public static class CellClient {
        private Integer x;
        private Integer y;

        private String colour;
        private Boolean fixed;

        CellClient(Cell cell) {
            this.fixed = cell.isFixed();
            this.x = cell.getX();
            this.y = cell.getY();
            this.colour = cell.getColour();

        }

        public Integer getX() {
            return x;
        }

        public void setX(Integer x) {
            this.x = x;
        }

        public Integer getY() {
            return y;
        }

        public void setY(Integer y) {
            this.y = y;
        }

        public String getColour() {
            return colour;
        }

        public void setColour(String colour) {
            this.colour = colour;
        }

        public Boolean getFixed() {
            return fixed;
        }

        public void setFixed(Boolean fixed) {
            this.fixed = fixed;
        }
    }

}
