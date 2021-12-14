package Engine.Tetromino;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import Engine.*;
import Engine.Navigation.Point;
import GUI.Palette;
import Engine.Navigation.Direction;
import Engine.Navigation.Orientation;

/**
 * Tetromino
 *
 * <p>
 * A Tetromino is a puzzle unit composed of four adjacent squares of the
 * same size. In the game of Tetris, there are seven possible shapes that a
 * {@code Tetromino} may take. For the purposes of this engine, these shapes
 * are represented by the characters I, J, L, O, S, T, and Z.
 *
 * <p>
 * This {@code Tetromino} is an immutable and disposable Object designed to
 * be replaced and collected by the JVM. Although this class is not marked
 * final, its only constructors are declared private so that it cannot be
 * subclassed externally. All instance fields are declared private and final,
 * and the {@code Tetromino} interface does not provide state-changing methods.
 * As such, each {@code Tetromino} is guaranteed to be immutable.
 *
 * @author Ellie Moore
 * @version 08.04.2020
 */
public class Tetromino extends TetrisGraphic {

    /*
     * Constants.
     */
    private static final Point I_SHAPE_ON_AXIS_SPAWN_POINT;
    private static final Point O_SHAPE_ON_AXIS_SPAWN_POINT;
    private static final Point        OFF_AXIS_SPAWN_POINT;

    /* init */
    static {
        I_SHAPE_ON_AXIS_SPAWN_POINT = new Point(160,32);
        O_SHAPE_ON_AXIS_SPAWN_POINT = new Point(160, 0);
        OFF_AXIS_SPAWN_POINT        = new Point(176,16);
    }

    /**
     * The {@code Square}s that constitute this {@code Tetromino}.
     */
    @NotNull
    private final List<Square> baseSquares;

    /**
     * The {@code Orientation} of this {@code Tetromino}.
     */
    @NotNull
    private final Orientation orientation;

    /**
     * The {@code Shape} of this {@code Tetromino}.
     */
    @NotNull
    private final Shape shape;

    /**
     * How far this {@code Tetromino} has fallen from its spawn point.
     */
    private final int depth;

    /**
     * A private, primary constructor for a {@code Tetromino} intended
     * for use by the {@code TetrominoFactory}.
     *
     * @param axis the axis of this {@code Tetromino}
     * @param shape the shape of this {@code Tetromino}
     * @param orientation the orientation of this {@code Tetromino}
     * @param color the color of this {@code Tetromino}
     * @param colorCode an identifier for use in recoloring
     * @param depth how many lines this {@code Tetromino} has fallen
     */
    private Tetromino(@NotNull final Point axis,
                      @NotNull final Shape shape,
                      @NotNull final Orientation orientation,
                      @NotNull final Color color,
                      final int colorCode,
                      final int depth){
        super(axis, color, colorCode);
        this.baseSquares = shape.assemble(
                orientation, axis, color, colorCode
        );
        this.orientation = orientation;
        this.shape = shape;
        this.depth = depth;
    }

    /**
     * A private, secondary constructor for a {@code Tetromino} intended
     * for internal subclass use.
     *
     * @param axis the axis of this {@code Tetromino}
     * @param baseSquares the squares that form this {@code Tetromino}
     * @param shape the shape of this {@code Tetromino}
     * @param orientation the orientation of this {@code Tetromino}
     * @param color the color of this {@code Tetromino}
     * @param colorCode an identifier for use in recoloring
     * @param depth how many lines this {@code Tetromino} has fallen
     */
    private Tetromino(@NotNull final Point axis,
                      @NotNull final List<Square> baseSquares,
                      @NotNull final Shape shape,
                      @NotNull final Orientation orientation,
                      @NotNull final Color color,
                      final int colorCode,
                      final int depth){
        super(axis, color, colorCode);
        this.baseSquares = baseSquares;
        this.orientation = orientation;
        this.shape = shape;
        this.depth = depth;
    }

    /**
     * Tetromino Factory
     *
     * <p>
     * A factory class for the {@code Tetromino}.
     */
    public static final class TetrominoFactory {

        /**
         * A public, null-safe instance.
         */
        public static final Tetromino NULL_TET = new Tetromino(
                Point.NULL, Shape.NULL, Orientation.FIRST, Color.WHITE, -1, -1
        ){

            @Override
            public final boolean isNull(){
                return true;
            }

            @Override
            public final Tetromino copyAt(final int x, final int y) {
                return this;
            }

            @Override
            public final Tetromino dematerialize() {
                return this;
            }

            @Override
            public final Tetromino rotate(){
                return this;
            }

            @Override
            public final Tetromino fall(){
                return this;
            }

            @Override
            public final Tetromino slide(final Direction d){
                return this;
            }

            @Override
            public final Tetromino respawn(){
                return this;
            }

            @Override
            public final Tetromino reColor(final Palette p){
                return this;
            }

        };

        /** This class may not be instantiated. */
        private TetrominoFactory(){
        }

        /**
         * A factory method to instantiate a {@code List} of {@code Tetromino}s in random order.
         *
         * @return a shuffled {@code List} of all seven {@code Tetromino}s
         */
        public static List<Tetromino> generateLineup(@NotNull final Palette palette) {
            final Orientation o = Orientation.FIRST;
            return Utility.shuffle(new Tetromino[]{
                    new Tetromino(Shape.I.getSpawnPoint(), Shape.I, o, genColor(palette), 0, 1),
                    new Tetromino(Shape.J.getSpawnPoint(), Shape.J, o, genColor(palette), 1, 1),
                    new Tetromino(Shape.L.getSpawnPoint(), Shape.L, o, genColor(palette), 2, 1),
                    new Tetromino(Shape.O.getSpawnPoint(), Shape.O, o, genColor(palette), 3, 1),
                    new Tetromino(Shape.S.getSpawnPoint(), Shape.S, o, genColor(palette), 4, 1),
                    new Tetromino(Shape.T.getSpawnPoint(), Shape.T, o, genColor(palette), 5, 1),
                    new Tetromino(Shape.Z.getSpawnPoint(), Shape.Z, o, genColor(palette), 6, 1)
            });
        }

        // To abbreviate.
        private static Color genColor(final Palette palette){
            return palette.getColor(Utility.rgen.nextInt(Palette.PALETTE_SIZE));
        }

    }

    /**
     * A method to instantiate a copy of this {@code Tetromino} at the specified
     * coordinates.
     *
     * @param x the x coordinate of the new {@code Tetromino}
     * @param y the y coordinate of the new {@code Tetromino}
     * @return a {@code Tetromino} at the specified coordinates or the instance if
     * the given coordinates lie beneath the x and y axes.
     */
    public Tetromino copyAt(final int x, final int y) {
        if(x < 0 || y < 0) return this;
        final Tetromino t = new Tetromino(
                new Point(x, y), shape, orientation, color,
                colorCode, (y >>> Utility.LOG_2_SQUARE_LENGTH) + 1
        );
        if(isGhost()) return t.dematerialize();
        return t;
    }

    /**
     * A method to instantiate a {@code GhostTetromino}, decorating this
     * {@code Tetromino} with translucent {@code GhostSquare}s.
     *
     * @return a {@code GhostTetromino}
     */
    public Tetromino dematerialize() {
        return new GhostTetromino(this);
    }

    private static final class GhostTetromino extends Tetromino {

        @NotNull
        private final Tetromino decoratedTetromino;

        private GhostTetromino(@NotNull final Tetromino t) {
            super(
                    t.axis, fadedSquares(t.baseSquares), t.shape,
                    t.orientation, t.color, t.colorCode, t.depth
            );
            decoratedTetromino = t;
        }

        private static List<Square> fadedSquares(final List<Square> baseSquares){
            final List<Square> replacementBaseSquares = new ArrayList<>();
            for(final Square s: baseSquares) replacementBaseSquares.add(Square.ghostInstance(s));
            return Collections.unmodifiableList(replacementBaseSquares);
        }

        @Override
        public final Tetromino dematerialize() {
            return this;
        }

        @Override
        public final Tetromino manifest(){
            return decoratedTetromino;
        }

        @Override
        public final boolean isGhost(){
            return true;
        }

    }

    /**
     * A factory method to instantiate a copy of this {@code Tetromino} with a rotated
     * {@code Orientation}.
     *
     * @return a rotated {@code Tetromino}
     */
    public Tetromino rotate() {
        final Tetromino t = new Tetromino(
                axis, shape, orientation.rotateClockwise(), color, colorCode, depth
        );
        if(isGhost()) return t.dematerialize();
        return t;
    }

    /**
     * A method to instantiate a copy one square-length below this {@code Tetromino}
     *
     * @return a {@code Tetromino} one {@code Square} below this {@code Tetromino}
     */
    public Tetromino fall() {
        final Tetromino t = new Tetromino(
                Direction.DOWN.traverse(axis), shape, orientation,
                color, colorCode, depth + 1
        );
        if(isGhost()) return t.dematerialize();
        return t;
    }

    /**
     * A method to instantiate a copy one square-length away from this {@code Tetromino}
     * in the specified {@code Direction}.
     *
     * @param d the {@code Direction} to be used
     * @return a {@code Tetromino} one {@code Square} away from the original
     */
    public Tetromino slide(@NotNull final Direction d) {
        final Tetromino t = new Tetromino(
                d.traverse(axis), shape, orientation, color, colorCode, depth
        );
        if(isGhost()) return t.dematerialize();
        return t;
    }

    /**
     * A method to instantiate a copy of the this {@code Tetromino} at its spawn
     * coordinates.
     *
     * @return a {@code Tetromino} at its spawn coordinates.
     */
    public Tetromino respawn(){
        final Tetromino t = new Tetromino(
                shape.getSpawnPoint(), shape, Orientation.FIRST, color, colorCode, 1
        );
        if(isGhost()) return t.dematerialize();
        return t;
    }

    /**
     * A method to instantiate a copy of this {@code Tetromino} with a color from the
     * given {@code palette} that according to the original color code.
     *
     * @return a re-colored {@code Tetromino}.
     */
    public Tetromino reColor(@NotNull final Palette p){
        final Tetromino t = new Tetromino(
                axis, shape, orientation, p.getColor(colorCode), colorCode, depth
        );
        if(isGhost()) return t.dematerialize();
        return t;
    }

    /**
     * @inheritDoc
     */
    @Override
    public final void paint(@NotNull final Graphics g){
        for(final Square s: baseSquares) s.paint(g);
    }

    /**
     * Exposes this {@code Tetromino}'s base squares.
     *
     * @return a {@code List} of the squares occupied by this {@code Tetromino}.
     */
    @NotNull
    public final List<Square> getBaseSquares(){
        return baseSquares;
    }

    /**
     * A method to indicate whether or not this {@code Tetromino} is an instance of
     * {@code NullTetromino}.
     *
     * @return whether or not this {@code Tetromino} is an instance of {@code NullTetromino}.
     */
    public boolean isNull(){
        return false;
    }

    /**
     * A method to indicate whether or not this {@code Tetromino} is an instance of
     * {@code GhostTetromino}.
     *
     * @return whether or not this {@code Tetromino} is an instance of {@code GhostTetromino}.
     */
    public boolean isGhost(){
        return false;
    }

    /**
     * Exposes this {@code Tetromino}'s depth.
     *
     * @return this {@code Tetromino}'s depth
     */
    public int getDepth(){
        return depth;
    }

    /**
     * A method to eject a {@code Tetromino} from a {@code GhostTetromino}.
     *
     * @return a {@code Tetromino}
     */
    public Tetromino manifest(){
        return this;
    }

    /**
     * Assembler
     */
    private interface Assembler {

        /**
         * A default implementation of {@code Assembler} to be referenced during instantiation
         * of the NULL {@code Shape}.
         */
        Assembler NULL_ASSEMBLER = new Assembler(){};

        /**
         * An implementation of {@code Assembler} to be referenced during instantiation of the
         * O {@code Shape}.
         */
        Assembler O_ASSEMBLER = new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                return List.of(
                        Square.defaultInstance(axis, color, colorCode),
                        Square.defaultInstance(new Point(axis.x - Utility.SQUARE_LENGTH, axis.y), color, colorCode),
                        Square.defaultInstance(new Point(axis.x, axis.y - Utility.SQUARE_LENGTH), color, colorCode),
                        Square.defaultInstance(new Point(
                                axis.x - Utility.SQUARE_LENGTH, axis.y - Utility.SQUARE_LENGTH
                        ), color, colorCode)
                );
            }
        };

        /**
         * A method to assemble a {@code List} of {@code Square}s that represent a
         * {@code Tetromino}
         *
         * @param axis the axis of the {@code Tetromino} under construction
         * @param color the color of the {@code Tetromino} under construction
         * @return a {@code List} of {@code Square}s that represent a {@code Tetromino}
         */
        default List<Square> assemble(final Point axis, final Color color, final int colorCode){
            return Collections.emptyList();
        }

    }

    /**
     * Shape
     *
     * <p>
     * Each {@code Shape} is responsible for assembling a {@code List} of four adjacent
     * {@code Square}s. Each {@code Shape} can be categorized by the location of its axis. An
     * on-axis {@code Shape} has an axis that rests along one of its edges, while an
     * off-axis {@code Shape} has an axis that lies within one of its squares.
     */
    private enum Shape {

        /**
         * On-axis.
         */
        I {
            /** @inheritDoc */
            @Override
            public Point getSpawnPoint(){
                return I_SHAPE_ON_AXIS_SPAWN_POINT;
            }

            @Override
            public List<Square> assemble(Orientation orientation, Point axis, Color color, int colorCode) {
                switch(orientation) {
                    case FIRST:
                        final int cly = axis.y - Utility.SQUARE_LENGTH;
                        return List.of(
                                Square.defaultInstance(new Point(axis.x, cly), color, colorCode),
                                Square.defaultInstance(new Point(axis.x + Utility.SQUARE_LENGTH, cly), color, colorCode),
                                Square.defaultInstance(new Point(axis.x - Utility.SQUARE_LENGTH, cly), color, colorCode),
                                Square.defaultInstance(new Point(
                                        axis.x - Utility.DOUBLE_SQUARE_LENGTH, cly), color, colorCode
                                )
                        );
                    case SECOND:
                        return List.of(
                                Square.defaultInstance(axis, color, colorCode),
                                Square.defaultInstance(
                                        new Point(axis.x, axis.y - Utility.DOUBLE_SQUARE_LENGTH), color, colorCode
                                ),
                                Square.defaultInstance(new Point(axis.x, axis.y - Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(axis.x, axis.y + Utility.SQUARE_LENGTH), color, colorCode)
                        );
                    case THIRD:
                        return List.of(
                                Square.defaultInstance(axis, color, colorCode),
                                Square.defaultInstance(new Point(axis.x + Utility.SQUARE_LENGTH, axis.y), color, colorCode),
                                Square.defaultInstance(new Point(axis.x - Utility.SQUARE_LENGTH, axis.y), color, colorCode),
                                Square.defaultInstance(
                                        new Point(axis.x - Utility.DOUBLE_SQUARE_LENGTH, axis.y), color, colorCode
                                )
                        );
                    case FOURTH:
                        final int clx = axis.x - Utility.SQUARE_LENGTH;
                        return List.of(
                                Square.defaultInstance(new Point(clx, axis.y), color, colorCode),
                                Square.defaultInstance(
                                        new Point(clx, axis.y - Utility.DOUBLE_SQUARE_LENGTH), color, colorCode
                                ),
                                Square.defaultInstance(new Point(clx, axis.y - Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(clx, axis.y + Utility.SQUARE_LENGTH), color, colorCode)
                        );
                    default:
                        return Collections.emptyList();
                }
            }
        },

        /**
         * Off-axis.
         */
        J {
            /** @inheritDoc */
            @Override
            public Point getSpawnPoint(){
                return OFF_AXIS_SPAWN_POINT;
            }

            @Override
            public List<Square> assemble(Orientation orientation, Point axis, Color color, int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                switch(orientation) {
                    case FIRST:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                                Square.defaultInstance(new Point(
                                        clx - Utility.SQUARE_LENGTH, cly - Utility.SQUARE_LENGTH), color, colorCode
                                ),
                                Square.defaultInstance(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode)
                        );
                    case SECOND:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(
                                        clx + Utility.SQUARE_LENGTH, cly - Utility.SQUARE_LENGTH), color, colorCode
                                )
                        );
                    case THIRD:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode),
                                Square.defaultInstance(new Point(
                                        clx + Utility.SQUARE_LENGTH, cly + Utility.SQUARE_LENGTH), color, colorCode
                                )
                        );
                    case FOURTH:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(
                                        clx - Utility.SQUARE_LENGTH, cly + Utility.SQUARE_LENGTH), color, colorCode
                                )
                        );
                    default:
                        return Collections.emptyList();
                }
            }
        },

        /**
         * Off-axis.
         */
        L {
            /** @inheritDoc */
            @Override
            public Point getSpawnPoint(){
                return OFF_AXIS_SPAWN_POINT;
            }

            @Override
            public List<Square> assemble(Orientation orientation, Point axis, Color color, int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                switch(orientation) {
                    case FIRST:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                                Square.defaultInstance(new Point(
                                        clx + Utility.SQUARE_LENGTH, cly - Utility.SQUARE_LENGTH), color, colorCode
                                ),
                                Square.defaultInstance(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode)
                        );
                    case SECOND:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(
                                        clx + Utility.SQUARE_LENGTH, cly + Utility.SQUARE_LENGTH), color, colorCode
                                )
                        );
                    case THIRD:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode),
                                Square.defaultInstance(new Point(
                                        clx - Utility.SQUARE_LENGTH, cly + Utility.SQUARE_LENGTH), color, colorCode
                                )
                        );
                    case FOURTH:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(
                                        clx - Utility.SQUARE_LENGTH, cly - Utility.SQUARE_LENGTH), color, colorCode
                                )
                        );
                    default:
                        return Collections.emptyList();
                }
            }
        },

        /**
         * On-axis.
         */
        O {
            /** @inheritDoc */
            @Override
            public Point getSpawnPoint(){
                return O_SHAPE_ON_AXIS_SPAWN_POINT;
            }

            @Override
            public List<Square> assemble(Orientation orientation, Point axis, Color color, int colorCode) {
                switch(orientation) {
                    case FIRST: case SECOND: case THIRD: case FOURTH:
                        return List.of(
                                Square.defaultInstance(axis, color, colorCode),
                                Square.defaultInstance(new Point(axis.x - Utility.SQUARE_LENGTH, axis.y), color, colorCode),
                                Square.defaultInstance(new Point(axis.x, axis.y - Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(
                                        axis.x - Utility.SQUARE_LENGTH, axis.y - Utility.SQUARE_LENGTH
                                ), color, colorCode)
                        );
                    default:
                        return Collections.emptyList();
                }
            }
        },

        /**
         * Off-axis.
         */
        S {
            /** @inheritDoc */
            @Override
            public Point getSpawnPoint(){
                return OFF_AXIS_SPAWN_POINT;
            }

            @Override
            public List<Square> assemble(Orientation orientation, Point axis, Color color, int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                switch(orientation) {
                    case FIRST:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(
                                        clx + Utility.SQUARE_LENGTH, cly - Utility.SQUARE_LENGTH), color, colorCode
                                )
                        );
                    case SECOND:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode),
                                Square.defaultInstance(new Point(
                                        clx + Utility.SQUARE_LENGTH, cly + Utility.SQUARE_LENGTH), color, colorCode
                                ),
                                Square.defaultInstance(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode)
                        );
                    case THIRD:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(
                                        clx - Utility.SQUARE_LENGTH, cly + Utility.SQUARE_LENGTH), color, colorCode
                                )
                        );
                    case FOURTH:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                                Square.defaultInstance(new Point(
                                        clx - Utility.SQUARE_LENGTH, cly - Utility.SQUARE_LENGTH), color, colorCode
                                ),
                                Square.defaultInstance(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode)
                        );
                    default:
                        return Collections.emptyList();
                }
            }
        },

        /**
         * Off-axis.
         */
        T {
            /** @inheritDoc */
            @Override
            public Point getSpawnPoint(){
                return OFF_AXIS_SPAWN_POINT;
            }

            @Override
            public List<Square> assemble(Orientation orientation, Point axis, Color color, int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                switch(orientation) {
                    case FIRST:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode)
                        );
                    case SECOND:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode)
                        );
                    case THIRD:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode)
                        );
                    case FOURTH:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode)
                        );
                    default:
                        return Collections.emptyList();
                }
            }
        },

        /**
         * Off-axis.
         */
        Z {
            /** @inheritDoc */
            @Override
            public Point getSpawnPoint(){
                return OFF_AXIS_SPAWN_POINT;
            }

            @Override
            public List<Square> assemble(Orientation orientation, Point axis, Color color, int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                switch(orientation) {
                    case FIRST:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(
                                        clx - Utility.SQUARE_LENGTH, cly - Utility.SQUARE_LENGTH), color, colorCode
                                ),
                                Square.defaultInstance(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode)
                        );
                    case SECOND:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode),
                                Square.defaultInstance(new Point(
                                        clx + Utility.SQUARE_LENGTH, cly - Utility.SQUARE_LENGTH), color, colorCode
                                )
                        );
                    case THIRD:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode),
                                Square.defaultInstance(new Point(
                                        clx + Utility.SQUARE_LENGTH, cly + Utility.SQUARE_LENGTH), color, colorCode
                                )
                        );
                    case FOURTH:
                        return List.of(
                                Square.defaultInstance(new Point(clx, cly), color, colorCode),
                                Square.defaultInstance(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                                Square.defaultInstance(new Point(
                                        clx - Utility.SQUARE_LENGTH, cly + Utility.SQUARE_LENGTH), color, colorCode
                                ),
                                Square.defaultInstance(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode)
                        );
                    default:
                        return Collections.emptyList();
                }
            }
        },

        /**
         * A NULL shape for use in constructing a {@code NullTetromino}.
         */
        NULL {
            @Override
            public Point getSpawnPoint(){
                return Point.NULL;
            }

            @Override
            public List<Square> assemble(Orientation orientation, Point axis, Color color, int colorCode) {
                return Collections.emptyList();
            }
        };

        /**
         * A method to express the spawn {@code Point} designated to each {@code Shape}.
         *
         * @return the {@code Point} at which the {@code Shape} will spawn
         */
        public abstract Point getSpawnPoint();

        public abstract List<Square> assemble(
            Orientation orientation, Point axis, Color color, int colorCode
        );

    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(Square s: baseSquares) sb.append(s).append("; ");
        return sb.toString();
    }

}
