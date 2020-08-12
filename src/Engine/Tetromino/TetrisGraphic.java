package Engine.Tetromino;

import Engine.Navigation.Point;
import GUI.Paintable;

import java.awt.*;

/**
 * Tetris Graphic
 *
 * <p>
 * An abstract parent class for {@code Tetromino} and its constituents.
 *
 * @author Ellie Moore
 * @version 08.06.2020
 */
public abstract class TetrisGraphic implements Paintable {

    /**
     * A reference location to orient this {@code Tetris Graphic}
     */
    protected final Point axis;

    /**
     * The color of this {@code tetrisGraphic}
     */
    protected final Color color;

    /**
     * The color code of this {@code tetrisGraphic} to be used in re-coloring
     */
    protected final int colorCode;

    /**
     * A protected constructor for a {@code TetrisGraphic}. This class
     * may only be extended from within the {@code Engine} package.
     *
     * @param p a reference location to orient this {@code Tetris Graphic}
     * @param color the color of this {@code tetrisGraphic}
     */
    protected TetrisGraphic(final Point p, final Color color, final int colorCode){
        this.axis = p;
        this.color = color;
        this.colorCode = colorCode;
    }

    /**
     * A method to expose the axis of this tetris {@code TetrisGraphic}.
     *
     * @return the axis of this {@code TetrisGraphic}
     */
    public final Point getAxis(){
        return axis;
    }

    /**
     * A method to expose the color of this tetris {@code TetrisGraphic}.
     *
     * @return the color of this {@code TetrisGraphic}
     */
    public final Color getColor() {
        return color;
    }

    /**
     * A method to expose the color code of this tetris {@code TetrisGraphic}.
     *
     * @return the color code of this {@code TetrisGraphic}
     */
    public final int getColorCode(){
        return colorCode;
    }

    /**
     * @inheritDoc
     */
    @Override
    public abstract void paint(Graphics g);

    /**
     * @inheritDoc
     */
    @Override
    public String toString(){
        return String.format("Axis: %s    Color: %s", axis, color);
    }

}
