package GUI;

import org.jetbrains.annotations.NotNull;
import java.awt.*;

/**
 * Paintable
 *
 * <p>
 * An interface to enforce the implementation of a method
 * responsible for painting the Object onto a {@code Component}.
 *
 * @author Ellie Moore
 * @version 08.06.2020
 */
public interface Paintable {

    /**
     * A method which paints the implementing Object
     * onto a component.
     *
     * @param g the {@code Graphics} Object to be used.
     */
    void paint(Graphics g);
}
