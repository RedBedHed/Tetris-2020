package Engine.Navigation;

import Engine.Utility;
import org.jetbrains.annotations.NotNull;

/**
 * Direction
 *
 * <p>
 * An enumeration of the three possible directions in which a {@code Tetromino} may travel.
 *
 * @author Ellie Moore
 * @version 08.05.2020
 */
public enum Direction {

    DOWN{
        /**
         * @inheritDoc
         */
        @Override
        public Point traverse(@NotNull final Point point) {
            return new Point(point.x, point.y + Utility.SQUARE_LENGTH);
        }
    },
    LEFT{
        /**
         * @inheritDoc
         */
        @Override
        public Point traverse(@NotNull final Point point) {
            return new Point(point.x - Utility.SQUARE_LENGTH, point.y);
        }
    },
    RIGHT{
        /**
         * @inheritDoc
         */
        @Override
        public Point traverse(@NotNull final Point point) {
            return new Point(point.x + Utility.SQUARE_LENGTH, point.y);
        }
    };

    /**
     * A method to return a point one square length from the argument in the
     * respective direction via polymorphism.
     *
     * @param point the initial {@code Point}
     * @return a point one square length from the argument
     */
    public abstract Point traverse(final Point point);
}
