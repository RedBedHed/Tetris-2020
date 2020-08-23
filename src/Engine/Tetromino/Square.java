package Engine.Tetromino;

import Engine.Navigation.Point;
import Engine.Utility;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Square
 *
 * <p>
 * The basic unit of a {@code Tetromino}.
 *
 * @author Ellie Moore
 * @version 08.06.2020
 */
public class Square extends TetrisGraphic {

    /**
     * A public constructor for a {@code Square}.
     *
     * @param upperLeftCorner the upper left corner of this {@code Square}
     * @param color the color of this {@code Square}
     * @param colorCode an identifier for use in recoloring
     */
    public Square(final Point upperLeftCorner, final Color color, final int colorCode) {
        super(upperLeftCorner, color, colorCode);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void paint(Graphics g){
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

}
