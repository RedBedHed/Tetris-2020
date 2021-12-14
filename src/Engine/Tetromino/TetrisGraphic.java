package Engine.Tetromino;

import Engine.Navigation.Point;
import GUI.Paintable;

import org.jetbrains.annotations.NotNull;

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
     * A reference location to orient this {@code Tetris Graphic}.
     */
    @NotNull
    protected final Point axis;

    /**
     * The color of this {@code tetrisGraphic}.
     */
    @NotNull
    protected final Color color;

    /**
     * The color code of this {@code tetrisGraphic} to be used in re-coloring.
     */
    protected final int colorCode;

    /**
     * A protected constructor for a {@code TetrisGraphic}. This class
     * may only be extended from within the {@code Engine} package.
     *
     * @param p a reference location to orient this {@code Tetris Graphic}
     * @param color the color of this {@code tetrisGraphic}
     * @param colorCode an identifier for use in recoloring
     */
    protected TetrisGraphic(@NotNull final Point p, @NotNull final Color color, final int colorCode){
        this.axis = p;
        this.color = color;
        this.colorCode = colorCode;
    }

    /**
     * A method to expose the axis of this tetris {@code TetrisGraphic}.
     *
     * @return the axis of this {@code TetrisGraphic}
     */
    public final @NotNull Point getAxis(){
        return axis;
    }

    /**
     * A method to expose the color of this tetris {@code TetrisGraphic}.
     *
     * @return the color of this {@code TetrisGraphic}
     */
    public final @NotNull Color getColor() {
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

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode(){
        return axis.hashCode();
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object other){
        if(this == other) return true;
        if(other == null) return false;
        if(!(other instanceof TetrisGraphic)) return false;
        TetrisGraphic cast = (TetrisGraphic) other;
        return this.hashCode() == cast.hashCode();
    }

}
