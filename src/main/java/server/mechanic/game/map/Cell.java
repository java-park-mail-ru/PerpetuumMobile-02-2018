package server.mechanic.game.map;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Cell {
    @JsonProperty(value = "x")
    private Integer coordX;
    @JsonProperty(value = "y")
    private Integer coordY;
    private String colour;
    private Boolean fixed;
    private Integer whoSetUserId;
    private Integer cubicId;
    private Integer place;


    public Boolean isFixed() {
        return fixed;
    }

    public void setFixed(Boolean fixed) {
        this.fixed = fixed;
    }

    @JsonGetter(value = "x")
    public Integer getCoordX() {
        return coordX;
    }

    @JsonSetter(value = "x")
    public void setCoordX(Integer coordX) {
        this.coordX = coordX;
    }

    @JsonGetter(value = "y")
    public Integer getCoordY() {
        return coordY;
    }

    @JsonSetter(value = "y")
    public void setCoordY(Integer coordY) {
        this.coordY = coordY;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public Integer getCubicId() {
        return cubicId;
    }

    public void setCubicId(Integer cubicId) {
        this.cubicId = cubicId;
    }


    public CellClient safeGet() {
        final CellClient cell = new CellClient(this);
        if (!cell.fixed) {
            cell.colour = null;
            cell.cubicId = null;
        }
        return cell;
    }

    public CellClient safeGetPool() {
        final CellClient cell = new CellClient(this);
        if (cell.fixed) {
            return null;
        }
        cell.coordX = null;
        cell.coordY = null;
        cell.place = null;
        return cell;
    }

    public Integer getWhoSetUserId() {
        return whoSetUserId;
    }

    public void setWhoSetUserId(Integer whoSetUserId) {
        this.whoSetUserId = whoSetUserId;
    }

    public Integer getPlace() {
        return place;
    }

    public void setPlace(Integer place) {
        this.place = place;
    }

    public static class CellClient {
        @JsonProperty(value = "x")
        private Integer coordX;

        @JsonProperty(value = "y")
        private Integer coordY;

        private String colour;
        private Boolean fixed;
        private Integer cubicId;
        private Integer place;

        CellClient(Cell cell) {
            this.fixed = cell.isFixed();
            this.coordX = cell.getCoordX();
            this.coordY = cell.getCoordY();
            this.colour = cell.getColour();
            this.cubicId = cell.getCubicId();
            this.place = cell.getPlace();
        }

        @JsonGetter(value = "x")
        public Integer getCoordX() {
            return coordX;
        }

        @JsonSetter(value = "x")
        public void setCoordX(Integer coordX) {
            this.coordX = coordX;
        }

        @JsonGetter(value = "y")
        public Integer getCoordY() {
            return coordY;
        }

        @JsonSetter(value = "y")
        public void setCoordY(Integer coordY) {
            this.coordY = coordY;
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

        public Integer getCubicId() {
            return cubicId;
        }

        public void setCubicId(Integer cubicId) {
            this.cubicId = cubicId;
        }

        public Integer getPlace() {
            return place;
        }

        public void setPlace(Integer place) {
            this.place = place;
        }
    }

}
