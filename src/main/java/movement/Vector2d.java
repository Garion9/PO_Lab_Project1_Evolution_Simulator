package movement;

import java.util.Objects;

public class Vector2d {
    public int x;
    public int y;

    public Vector2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }

    public boolean follows(Vector2d other) {
        return (this.x >= other.x && this.y >= other.y);
    }

    public boolean precedes(Vector2d other) {
        return (this.x <= other.x && this.y <= other.y);
    }

    public Vector2d add(Vector2d other) {
        return new Vector2d(this.x + other.x,this.y + other.y );
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Vector2d))
            return false;
        Vector2d that = (Vector2d) other;
        return (this.x == that.x && this.y == that.y) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    public boolean inArea(Vector2d bottomLeft, Vector2d topRight) {
        return this.follows(bottomLeft) && this.precedes(topRight);
    }

    public Vector2d parsePosition(Vector2d lowerLimit, Vector2d upperLimit) {
        if (!this.inArea(lowerLimit, upperLimit)) {
            if (this.x < lowerLimit.x) this.x = upperLimit.x;
            if (this.y < lowerLimit.y) this.y = upperLimit.y;
            if (this.x > upperLimit.x) this.x = lowerLimit.x;
            if (this.y > upperLimit.y) this.y = lowerLimit.y;
        }
        return this;
    }
}
