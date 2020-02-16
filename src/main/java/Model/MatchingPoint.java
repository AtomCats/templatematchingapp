package Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchingPoint {

    private Point mainPoint;
    private ArrayList<Point> surroundingPoints = new ArrayList<Point>(8);

    public MatchingPoint(Point mainPoint) {
        this.setMainPoint(mainPoint);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchingPoint that = (MatchingPoint) o;
        return getMainPoint().equals(that.getMainPoint()) &&
                getSurroundingPoints().equals(that.getSurroundingPoints());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMainPoint(), getSurroundingPoints());
    }
}
