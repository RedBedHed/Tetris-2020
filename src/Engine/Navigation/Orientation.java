package Engine.Navigation;

/**
 * Orientation
 *
 * <p>
 * An enumeration of the four possible orientations that a {@code Tetromino} may possess.
 *
 * @author Ellie Moore
 * @version 08.06.2020
 */
public enum Orientation {
    FIRST {
        /** @inheritDoc */
        @Override public Orientation rotateClockwise() { return SECOND; }
    },
    SECOND {
        /** @inheritDoc */
        @Override public Orientation rotateClockwise() { return THIRD; }
    },
    THIRD {
        /** @inheritDoc */
        @Override public Orientation rotateClockwise() { return FOURTH; }
    },
    FOURTH {
        /** @inheritDoc */
        @Override public Orientation rotateClockwise() { return FIRST; }
    };

    /**
     * A method to get the next {@code Orientation} in the clockwise direction.
     *
     * @return the next {@code Orientation}
     */
    public abstract Orientation rotateClockwise();
}