package Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Point {

    private int x;
    private int y;
    private Color color;

    public Point(int x, int y) {
        this.setX(x);
        this.setY(y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
        Point point = (Point) o;
        return getX() == point.getX() &&
                getY() == point.getY() &&
                getColor().equals(point.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getColor());
    }
}
