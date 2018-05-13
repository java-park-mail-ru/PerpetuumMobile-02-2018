package server.mechanic.map;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class Coord {

    Coord(@JsonProperty("x") double x, @JsonProperty("y") double y) {
        this.x = x;
        this.y = y;
    }

    private final double x;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    private final double y;

    @Override
    public String toString() {
        return '{'
                + "x=" + x
                + ", y=" + y
                + '}';
    }

    @SuppressWarnings("NewMethodNamingConvention")
    @NotNull
    public static Coord of(double x, double y) {
        return new Coord(x, y);
    }
}
