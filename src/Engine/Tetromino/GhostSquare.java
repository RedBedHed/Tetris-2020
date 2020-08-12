package Engine.Tetromino;

import Engine.Utility;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Ghost Square
 *
 * <p>
 * A translucent square.
 *
 * @author Ellie Moore
 * @version 08.05.2020
 */
public final class GhostSquare extends Square {

    /**
     * A public constructor for {@code GhostSquare} which
     * instantiates a translucent "copy" of the given {@code Square}
     *
     * @param square the square to be copied
     */
    public GhostSquare(final Square square) {
        super(square.axis, square.color, square.colorCode);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void paint(Graphics g){
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
