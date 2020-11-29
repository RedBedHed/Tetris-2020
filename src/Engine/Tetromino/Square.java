package Engine.Tetromino;

import Engine.Navigation.Point;
import Engine.Utility;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Square
 *
 * <p>
 * A {@code Square} is the basic unit of a {@code Tetromino}. Although this class
 * is not final, its constructor is private. Therefore, this class may not be subclassed
 * externally.
 *
 * @author Ellie Moore
 * @version 08.06.2020
 */
public class Square extends TetrisGraphic {

    /**
     * A private constructor for a {@code Square}.
     *
     * @param upperLeftCorner the upper left corner of this {@code Square}
     * @param color the color of this {@code Square}
     * @param colorCode an identifier for use in recoloring
     */
    private Square(@NotNull final Point upperLeftCorner,
                   @NotNull final Color color,
                   final int colorCode) {
        super(upperLeftCorner, color, colorCode);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void paint(@NotNull final Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.BLACK);
        g2.fill(new RoundRectangle2D.Double(
                axis.x + Utility.SQUARE_BUFFER, axis.y + Utility.SQUARE_BUFFER,
                Utility.OUTER_SQUARE_LENGTH, Utility.OUTER_SQUARE_LENGTH,
                Utility.SQUARE_BUFFER, Utility.SQUARE_BUFFER
        ));
        g2.setPaint(color);
        g2.fill(new RoundRectangle2D.Double(
                axis.x, axis.y, Utility.INNER_SQUARE_LENGTH, Utility.INNER_SQUARE_LENGTH,
                Utility.SQUARE_BUFFER, Utility.SQUARE_BUFFER
        ));
    }

    /**
     * A factory method to produce a default {@code Square}.
     *
     * @param upperLeftCorner the upper left corner of this {@code Square}
     * @param color the color of this {@code Square}
     * @param colorCode an identifier for use in recoloring
     * @return a new {@code Square}
     */
    public static Square defaultInstance(@NotNull final Point upperLeftCorner,
                                         @NotNull final Color color,
                                         final int colorCode){
        return new Square(upperLeftCorner, color, colorCode);
    }

    /**
     * A factory method to produce a translucent {@code Ghost Square}.
     *
     * @param square square the square to be copied
     * @return a new {@code GhostSquare}
     */
    public static Square ghostInstance(@NotNull final Square square){
        return new GhostSquare(square);
    }

    private static final class GhostSquare extends Square {

        private GhostSquare(@NotNull final Square square) {
            super(square.axis, square.color, square.colorCode);
        }

        @Override
        public void paint(@NotNull final Graphics g){
            final float OPACITY = 0.4f;
            Graphics2D g2 = (Graphics2D) g;
            g2.setComposite(AlphaComposite.SrcOver.derive(OPACITY));
            g2.setPaint(color);
            g2.fill(new RoundRectangle2D.Double(
                    axis.x + Utility.SQUARE_BUFFER, axis.y + Utility.SQUARE_BUFFER,
                    Utility.OUTER_SQUARE_LENGTH, Utility.OUTER_SQUARE_LENGTH,
                    Utility.SQUARE_BUFFER, Utility.SQUARE_BUFFER
            ));
            g2.setPaint(Color.DARK_GRAY);
            g2.fill(new RoundRectangle2D.Double(
                    axis.x, axis.y, Utility.INNER_SQUARE_LENGTH, Utility.INNER_SQUARE_LENGTH,
                    Utility.SQUARE_BUFFER, Utility.SQUARE_BUFFER
            ));
            g2.setComposite(AlphaComposite.SrcOver);
        }

    }

}
