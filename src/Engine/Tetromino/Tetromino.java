package Engine.Tetromino;

import java.awt.*;
import java.util.*;
import java.util.List;

import Engine.*;
import Engine.Navigation.Point;
import Engine.Utility.ConciseArrayList;
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
    private final List<Square> baseSquares;

    /**
     * The {@code Orientation} of this {@code Tetromino}.
     */
    private final Orientation orientation;

    /**
     * The {@code Shape} of this {@code Tetromino}.
     */
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
    private Tetromino(final Point axis,
                      final Shape shape,
                      final Orientation orientation,
                      final Color color,
                      final int colorCode,
                      final int depth){
        super(axis, color, colorCode);
        this.baseSquares = shape.assemblyLines.get(
                orientation.ordinal()
        ).assemble(axis, color, colorCode);
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
    private Tetromino(final Point axis,
                      final List<Square> baseSquares,
                      final Shape shape,
                      final Orientation orientation,
                      final Color color,
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
     * A factory class for the {@code Tetromino}. All instances of {@code Tetromino}
     * must be instantiated via static methods enclosed within this class.
     */
    public static final class TetrominoFactory {

        /**
         * A public, null-safe instance.
         */
        public static final Tetromino NULL_TET = new NullTetromino();

        private static final class NullTetromino extends Tetromino {

            private NullTetromino() {
                super(Point.NULL, Shape.NULL, Orientation.FIRST, Color.WHITE, -1, -1);
            }

            @Override
            public final boolean isNull(){
                return true;
            }

        }

        /** This class may not be instantiated. */
        private TetrominoFactory(){
        }

        /**
         * A factory method to instantiate a {@code List} of {@code Tetromino}s in random order.
         *
         * @return a shuffled {@code List} of all seven {@code Tetromino}s
         */
        public static List<Tetromino> generateLineup(final Palette palette) {
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

        /**
         * A factory method to instantiate a {@code Tetromino} with a rotated {@code Orientation}.
         *
         * @param t the {@code Tetromino} to be rotated
         * @return a rotated {@code Tetromino}
         */
        public static Tetromino rotatingInstance(final Tetromino t) {
            if (t == null || t.isNull()) return NULL_TET;
            final Orientation o = t.orientation.rotateClockwise();
            return new Tetromino(t.axis, t.shape, o, t.color, t.colorCode, t.depth);
        }

        /**
         * A factory method to instantiate a new {@code Tetromino} one square length below the
         * argument.
         *
         * @param t the {@code Tetromino} to be moved
         * @return a {@code Tetromino} one {@code Square} below the original
         */
        public static Tetromino fallingInstance(final Tetromino t) {
            if (t == null || t.isNull()) return NULL_TET;
            final Point p = Direction.DOWN.traverse(t.axis);
            return new Tetromino(p, t.shape, t.orientation, t.color, t.colorCode, t.depth + 1);
        }

        /**
         * A factory method to instantiate a {@code Tetromino} with an updated location in the
         * specified {@code Direction}.
         *
         * @param t the {@code Tetromino} to be moved
         * @param d the {@code Direction} to be used
         * @return a {@code Tetromino} one {@code Square} away from the original
         */
        public static Tetromino slidingInstance(final Tetromino t, final Direction d) {
            if (t == null || t.isNull()) return NULL_TET;
            return new Tetromino(
                    d.traverse(t.axis), t.shape, t.orientation, t.color, t.colorCode, t.depth
            );
        }

        /**
         * A method to instantiate a {@code GhostTetromino}, decorating the given
         * {@code Tetromino} with translucent {@code GhostSquare}s.
         *
         * @param t the tetromino to be decorated
         * @return a {@code GhostTetromino}
         */
        public static Tetromino ghostInstance(final Tetromino t) {
            if (t == null || t.isNull()) return NULL_TET;
            return new GhostTetromino(t);
        }

        private static final class GhostTetromino extends Tetromino {

            private final Tetromino decoratedTetromino;

            private GhostTetromino(final Tetromino t) {
                super(
                        t.axis, fadedSquares(t.baseSquares), t.shape,
                        t.orientation, t.color, t.colorCode, t.depth
                );
                decoratedTetromino = t;
            }

            private static List<Square> fadedSquares(final List<Square> baseSquares){
                final List<Square> replacementBaseSquares = new ArrayList<>();
                for(final Square s: baseSquares) replacementBaseSquares.add(new GhostSquare(s));
                return Collections.unmodifiableList(replacementBaseSquares);
            }

            @Override
            public final Tetromino manifest(){
                return decoratedTetromino;
            }

        }

        /**
         * A factory method to instantiate a copy of the given {@code Tetromino} at the specified
         * coordinates.
         *
         * @param x the x coordinate of the new {@code Tetromino}
         * @param y the y coordinate of the new {@code Tetromino}
         * @param t the {@code Tetromino} to be copied
         * @return a {@code Tetromino} at the specified coordinates or {@code NullTetromino} if
         * the given coordinates lie beneath the x and y axes.
         */
        public static Tetromino copyAt(final int x, final int y, final Tetromino t){
            if(x < 0 || y < 0 || t == null || t.isNull()) return NULL_TET;
            return new Tetromino(
                    new Point(x, y), t.shape, t.orientation, t.color,
                    t.colorCode, (y >>> Utility.LOG_2_SQUARE_LENGTH) + 1
            );
        }

        /**
         * A factory method to instantiate a copy of the given {@code Tetromino} at its spawn
         * coordinates.
         *
         * @param t the {@code Tetromino} to be re-spawned
         * @return a {@code Tetromino} at its spawn coordinates.
         */
        public static Tetromino respawn(final Tetromino t){
            if (t == null || t.isNull()) return NULL_TET;
            return new Tetromino(
                    t.shape.getSpawnPoint(), t.shape, Orientation.FIRST, t.color, t.colorCode, 1
            );
        }

        /**
         * A factory method to instantiate a copy of the given {@code Tetromino} with a color from the
         * given {@code palette} that matches the original color code.
         *
         * @param t the {@code Tetromino} to be re-colored
         * @return a re-colored {@code Tetromino}.
         */
        public static Tetromino reColor(final Tetromino t, final Palette p){
            if (t == null || t.isNull()) return NULL_TET;
            return new Tetromino(
                    t.axis, t.shape, t.orientation, p.getColor(t.colorCode), t.colorCode, t.depth
            );
        }

    }

    /**
     * @inheritDoc
     */
    @Override
    public final void paint(final Graphics g){
        for(final Square s: baseSquares) s.paint(g);
    }

    /**
     * Exposes this {@code Tetromino}'s base squares.
     *
     * @return a {@code List} of the squares occupied by this {@code Tetromino}.
     */
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
                        new Square(axis, color, colorCode),
                        new Square(new Point(axis.x - Utility.SQUARE_LENGTH, axis.y), color, colorCode),
                        new Square(new Point(axis.x, axis.y - Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(
                                axis.x - Utility.SQUARE_LENGTH, axis.y - Utility.SQUARE_LENGTH
                        ), color, colorCode)
                );
            }
        };

        /**
         * A method to assemble a {@code List} of {@code Square}s that represent a
         * {@code Engine.Tetromino}
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
     * {@code Square}s. Each {@code shape} can be categorized by the location of its axis. An
     * on-axis {@code Shape} has an axis that rests along one of its edges, while an
     * off-axis {@code Shape} has an axis that lies within one of its squares.
     */
    private enum Shape {

        /**
         * On-axis.
         */
        I (Collections.unmodifiableList(new ConciseArrayList<Assembler>().with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int cly = axis.y - Utility.SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(axis.x, cly), color, colorCode),
                        new Square(new Point(axis.x + Utility.SQUARE_LENGTH, cly), color, colorCode),
                        new Square(new Point(axis.x - Utility.SQUARE_LENGTH, cly), color, colorCode),
                        new Square(new Point(axis.x - Utility.DOUBLE_SQUARE_LENGTH, cly), color, colorCode)
                );
            }
        }).with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                return List.of(
                        new Square(axis, color, colorCode),
                        new Square(new Point(axis.x, axis.y - Utility.DOUBLE_SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(axis.x, axis.y - Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(axis.x, axis.y + Utility.SQUARE_LENGTH), color, colorCode)
                );
            }
        }).with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                return List.of(
                        new Square(axis, color, colorCode),
                        new Square(new Point(axis.x + Utility.SQUARE_LENGTH, axis.y), color, colorCode),
                        new Square(new Point(axis.x - Utility.SQUARE_LENGTH, axis.y), color, colorCode),
                        new Square(new Point(axis.x - Utility.DOUBLE_SQUARE_LENGTH, axis.y), color, colorCode)
                );
            }
        }).with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, axis.y), color, colorCode),
                        new Square(new Point(clx, axis.y - Utility.DOUBLE_SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(clx, axis.y - Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(clx, axis.y + Utility.SQUARE_LENGTH), color, colorCode)
                );
            }
        }))) {
            /** @inheritDoc */
            @Override
            public Point getSpawnPoint(){
                return I_SHAPE_ON_AXIS_SPAWN_POINT;
            }
        },

        /**
         * Off-axis.
         */
        J (Collections.unmodifiableList(new ConciseArrayList<Assembler>().with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                        new Square(new Point(
                                clx - Utility.SQUARE_LENGTH, cly - Utility.SQUARE_LENGTH), color, colorCode
                        ),
                        new Square(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode)
                );
            }
        }).with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(
                        clx + Utility.SQUARE_LENGTH, cly - Utility.SQUARE_LENGTH), color, colorCode
                        )
                );
            }
        }).with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                        new Square(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode),
                        new Square(new Point(
                        clx + Utility.SQUARE_LENGTH, cly + Utility.SQUARE_LENGTH), color, colorCode
                        )
                );
            }
        }).with(new Assembler(){
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(
                                clx - Utility.SQUARE_LENGTH, cly + Utility.SQUARE_LENGTH), color, colorCode
                        )
                );
            }
        }))) {
            /** @inheritDoc */
            @Override
            public Point getSpawnPoint(){
                return OFF_AXIS_SPAWN_POINT;
            }
        },

        /**
         * Off-axis.
         */
        L (Collections.unmodifiableList(new ConciseArrayList<Assembler>().with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                        new Square(new Point(
                                clx + Utility.SQUARE_LENGTH, cly - Utility.SQUARE_LENGTH), color, colorCode
                        ),
                        new Square(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode)
                );
            }
        }).with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(
                                clx + Utility.SQUARE_LENGTH, cly + Utility.SQUARE_LENGTH), color, colorCode
                        )
                );
            }
        }).with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                        new Square(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode),
                        new Square(new Point(
                                clx - Utility.SQUARE_LENGTH, cly + Utility.SQUARE_LENGTH), color, colorCode
                        )
                );
            }
        }).with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(
                                clx - Utility.SQUARE_LENGTH, cly - Utility.SQUARE_LENGTH), color, colorCode
                        )
                );
            }
        }))) {
            /** @inheritDoc */
            @Override
            public Point getSpawnPoint(){
                return OFF_AXIS_SPAWN_POINT;
            }
        },

        /**
         * On-axis.
         */
        O (Collections.unmodifiableList(
                new ConciseArrayList<Assembler>()
                .with(Assembler.O_ASSEMBLER)
                .with(Assembler.O_ASSEMBLER)
                .with(Assembler.O_ASSEMBLER)
                .with(Assembler.O_ASSEMBLER)
        )) {
            /** @inheritDoc */
            @Override
            public Point getSpawnPoint(){
                return O_SHAPE_ON_AXIS_SPAWN_POINT;
            }
        },

        /**
         * Off-axis.
         */
        S (Collections.unmodifiableList(new ConciseArrayList<Assembler>().with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                        new Square(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(
                                clx + Utility.SQUARE_LENGTH, cly - Utility.SQUARE_LENGTH), color, colorCode
                        )
                );
            }
        }).with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode),
                        new Square(new Point(
                                clx + Utility.SQUARE_LENGTH, cly + Utility.SQUARE_LENGTH), color, colorCode
                        ),
                        new Square(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode)
                );
            }
        }).with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode),
                        new Square(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(
                                clx - Utility.SQUARE_LENGTH, cly + Utility.SQUARE_LENGTH), color, colorCode
                        )
                );
            }
        }).with(new Assembler(){
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                        new Square(new Point(
                                clx - Utility.SQUARE_LENGTH, cly - Utility.SQUARE_LENGTH), color, colorCode
                        ),
                        new Square(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode)
                );
            }
        }))) {
            /** @inheritDoc */
            @Override
            public Point getSpawnPoint(){
                return OFF_AXIS_SPAWN_POINT;
            }
        },

        /**
         * Off-axis.
         */
        T (Collections.unmodifiableList(new ConciseArrayList<Assembler>().with(new Assembler(){
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                        new Square(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode)
                );
            }
        }).with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode),
                        new Square(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode)
                );
            }
        }).with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                        new Square(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode)
                );
            }
        }).with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                        new Square(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode)
                );
            }
        }))) {
            /** @inheritDoc */
            @Override
            public Point getSpawnPoint(){
                return OFF_AXIS_SPAWN_POINT;
            }
        },

        /**
         * Off-axis.
         */
        Z (Collections.unmodifiableList(new ConciseArrayList<Assembler>().with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(
                                clx - Utility.SQUARE_LENGTH, cly - Utility.SQUARE_LENGTH), color, colorCode
                        ),
                        new Square(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode)
                );
            }
        }).with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(clx + Utility.SQUARE_LENGTH, cly), color, colorCode),
                        new Square(new Point(
                                clx + Utility.SQUARE_LENGTH, cly - Utility.SQUARE_LENGTH), color, colorCode
                        )
                );
            }
        }).with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                        new Square(new Point(clx, cly + Utility.SQUARE_LENGTH), color, colorCode),
                        new Square(new Point(
                                clx + Utility.SQUARE_LENGTH, cly + Utility.SQUARE_LENGTH), color, colorCode
                        )
                );
            }
        }).with(new Assembler() {
            @Override
            public List<Square> assemble(final Point axis, final Color color, final int colorCode) {
                final int clx = axis.x - Utility.HALF_SQUARE_LENGTH;
                final int cly = axis.y - Utility.HALF_SQUARE_LENGTH;
                return List.of(
                        new Square(new Point(clx, cly), color, colorCode),
                        new Square(new Point(clx - Utility.SQUARE_LENGTH, cly), color, colorCode),
                        new Square(new Point(
                                clx - Utility.SQUARE_LENGTH, cly + Utility.SQUARE_LENGTH), color, colorCode
                        ),
                        new Square(new Point(clx, cly - Utility.SQUARE_LENGTH), color, colorCode)
                );
            }
        }))) {
            /** @inheritDoc */
            @Override
            public Point getSpawnPoint(){
                return OFF_AXIS_SPAWN_POINT;
            }
        },

        /**
         * A NULL shape for use in constructing a {@code NullTetromino}.
         */
        NULL (Collections.unmodifiableList(
                new ConciseArrayList<Assembler>()
                .with(Assembler.NULL_ASSEMBLER)
                .with(Assembler.NULL_ASSEMBLER)
                .with(Assembler.NULL_ASSEMBLER)
                .with(Assembler.NULL_ASSEMBLER))) {
            @Override
            public Point getSpawnPoint(){
                return Point.NULL;
            }
        };

        /**
         * A {@code List} of {@code Assembler} implementations with indices that correspond
         * to each {@code Orientation} ordinal value.
         */
        private final List<Assembler> assemblyLines;

        /**
         * A private constructor for a {@code Shape}.
         */
        Shape(final List<Assembler> assemblyLines){
            this.assemblyLines = assemblyLines;
        }

        /**
         * A method to express the spawn {@code Point} designated to each {@code Shape}.
         *
         * @return the {@code Point} at which the {@code Shape} will spawn
         */
        public abstract Point getSpawnPoint();

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
