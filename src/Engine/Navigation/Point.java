package Engine.Navigation;

/**
 * Point
 *
 * <p>
 * A re-creation of awt Point.
 *
 * @author ELlie Moore
 * @version 05.08.2020
 */
public final class Point {

    /**
     * A null-safe sentinel instance of {@code Point}.
     */
    public static final Point NULL = new Point(-1, -1);

    /**
     * Public coordinate fields
     */
    public final int x;
    public final int y;

    /**
     * A constructor to allow for final initialization.
     */
    public Point(final int x, final int y){
        this.x = x;
        this.y = y;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object other){
        if(this == other) return true;
        if(other == null) return false;
        if(!(other instanceof Point)) return false;
        Point cast = (Point) other;
        return this.x == cast.x && this.y == cast.y;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        int hash = 1;
        hash = 31 * hash + x;
        return 31 * hash + y;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString(){
        return "[" + x + ", " + y + "]";
    }

}