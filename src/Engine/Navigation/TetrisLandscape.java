package Engine.Navigation;

import Engine.Tetromino.Tetromino;
import Engine.Tetromino.Square;
import Engine.Utility;
import GUI.Paintable;
import GUI.Palette;

import org.jetbrains.annotations.NotNull;

import java.awt.Graphics;
import java.util.*;
import java.util.List;

/**
 * Tetris Landscape
 *
 * <p>
 * {@code TetrisLandscape} is a package of landscape and score data for the game of Tetris.
 * It is an immutable and disposable Object designed to be replaced and collected by the JVM.
 *
 * @author Ellie Moore
 * @version 08.08.2020
 */
public final class TetrisLandscape implements Paintable {

    /* Constants */
    private static final int LANDSCAPE_INITIAL_SIZE = 267;
    private static final int HISTORY_INITIAL_SIZE   =  20;
    private static final int POINTS_CACHE_SIZE      =  10;
    private static final int HISTORY_ROW_SIZE       =  10;
    private static final int LEFT_BOUNDARY          = -32;
    private static final int RIGHT_BOUNDARY         = 320;
    private static final Set<Point>  INITIAL_POINTS_CACHE;
    static {
        INITIAL_POINTS_CACHE = initPointsCache();
    }

    /**
     * A two dimensional {@code List} to hold each {@code Square} that belongs
     * to the landscape. There may be up to eight empty rows in this history.
     * These rows will not be considered during copying or painting operations.
     */
    @NotNull
    private final List<List<Square>> history;

    /**
     * A {@code Set} of all {@code Point}s belonging to the landscape for use in
     * impact detection.
     */
    @NotNull
    private final Set<Point> landscape;

    /**
     * The score of this {@code TetrisLandscape}.
     */
    private final int score;

    /**
     * A private constructor for a {@code TetrisLandscape} Object.
     *
     * @param history a two-dimensional {@code List} of {@code Squares} for use in painting
     * @param landscape a {@code Set} of {@code Point}s for impact detection
     * @param score an integer value to represent the score of this {@code TetrisLandscape}
     */
    private TetrisLandscape(@NotNull final List<List<Square>> history,
                            @NotNull final Set<Point> landscape,
                            final int score){
        this.history = history;
        this.landscape = landscape;
        this.score = score;
    }

    /*
     * A method to initialize the INITIAL POINTS CACHE.
     */
    private static Set<Point> initPointsCache(){
        final Set<Point> landscape = new HashSet<>(POINTS_CACHE_SIZE);
        for(int i = 0; i <= Utility.GRID_WIDTH; i += Utility.SQUARE_LENGTH)
            landscape.add(new Point(i, Utility.GRID_HEIGHT));
        return Collections.unmodifiableSet(landscape);
    }

    /**
     * A static factory method to instantiate a new {@code TetrisLandscape} Object
     * with an empty history, a landscape that consists of the bottom row of the
     * grid, and a score of 0.
     *
     * @return a new {@code TetrisLandscape} object with fields set to default values
     */
    public static TetrisLandscape defaultInstance(){
        return new TetrisLandscape(
                Collections.emptyList(), INITIAL_POINTS_CACHE, 0
        );
    }

    /**
     * A method to instantiate an updated {@code TetrisLandscape} upon {@code Tetromino}
     * contact. This method handles row-clearing with an O(N) algorithm where N is
     * approximately 400 in the worst case. The algorithm makes two passes over
     * the history {@code List} (once over the previous history and once over the
     * current history), and ensures that all updated data is unmodifiable prior
     * to instantiation. This algorithm sacrifices efficiency for immutability.
     *
     * <p>
     * This method also handles scoring with an O(N) algorithm where N is the current
     * level. Scores are calculated using the original Nintendo scoring system:
     * <ul>
     *     <li> 1 line cleared = 40 * (current level) </li>
     *     <li> 2 lines cleared = 100 * (current level) </li>
     *     <li> 3 lines cleared = 300 * (current level) </li>
     *     <li> 4 lines cleared = 1200 * (current level) </li>
     * </ul>
     *
     * @param t the current {@code Tetromino} to be added
     * @param level the current level number
     * @return an updated {@code TetrisLandscape} object
     */
    public TetrisLandscape merge(@NotNull final Tetromino t, final int level) {

        /*
         * Initialize a mutable copy of an immutable two-dimensional "history"
         * List with four empty Lists added as a buffer. Four is the maximum number of
         * blocks that a Tetromino can contribute to the landscape in the northward
         * direction.
         * O(N)
         */
        final int MAXIMUM_TET_HEIGHT = 4;
        final List<List<Square>> rh = new ArrayList<>(HISTORY_INITIAL_SIZE);
        for(final List<Square> ls: history) {
            if(ls.isEmpty()) break;
            rh.add(new ArrayList<>(ls));
        }
        for(int j = 0; j < MAXIMUM_TET_HEIGHT; j++) rh.add(new ArrayList<>());

        /*
         * Add the current Tetromino's Squares to the replacement history.
         * O(4)
         */
        final int LAST_INDEX = HISTORY_INITIAL_SIZE - 1;
        for(final Square s: t.getBaseSquares()) {
            final int r = ((s.getAxis().y < 0) || (s.getAxis().y >= Utility.GRID_HEIGHT)) ?
                    HISTORY_INITIAL_SIZE:
                    LAST_INDEX - (s.getAxis().y >>> Utility.LOG_2_SQUARE_LENGTH);
            if(r >= rh.size()) throw new RuntimeException(
                    "The given Tetromino must be adjacent to the given landscape."
            );
            rh.get(r).add(s);
        }

        /*
         * Initialize the bottom row of the replacement landscape. O(10)
         */
        final Set<Point> rl = new HashSet<>(LANDSCAPE_INITIAL_SIZE);
        rl.addAll(INITIAL_POINTS_CACHE);

        /*
         * Omit any history row with 10 Squares or more. Shift down.
         * O(N)
         */
        int lineCount = 0;
        for(int i = 0, j = i; j < rh.size(); i++, j++) {
            while(j < rh.size() && rh.get(j).size() >= HISTORY_ROW_SIZE) {
                j++; lineCount++;
            }
            final List<Square> rr = new ArrayList<>();
            for (final Square s : rh.get(j)) {
                final Point p = new Point(
                        s.getAxis().x, s.getAxis().y +
                        (lineCount << Utility.LOG_2_SQUARE_LENGTH)
                );
                rr.add(Square.defaultInstance(p, s.getColor(), s.getColorCode()));
                rl.add(p);
            }
            rh.set(i, Collections.unmodifiableList(rr));
        }

        /*
         * Score the current landscape.
         */
        int s = t.getDepth() + score;
        switch(lineCount){
            case 4: for(int k = 0; k <= level; k++) s += 1200; break;
            case 3: for(int k = 0; k <= level; k++) s +=  300; break;
            case 2: for(int k = 0; k <= level; k++) s +=  100; break;
            case 1: for(int k = 0; k <= level; k++) s +=   40; break;
        }

        /*
         * Initialize.
         */
        return new TetrisLandscape(
                Collections.unmodifiableList(rh), Collections.unmodifiableSet(rl), s
        );

    }

    /**
     * A method to copy this {@code TetrisLandscape object}, re-coloring it in the process.
     *
     * @param p the palette to use in re-coloring
     * @return a re-colored instance of {@code TetrisLandscape}
     */
    public TetrisLandscape reColor(@NotNull final Palette p){
        final List<List<Square>> rh = new ArrayList<>();
        for(final List<Square> ls: history) {
            if(ls.isEmpty()) break;
            final List<Square> rs = new ArrayList<>();
            for(final Square s: ls) rs.add(Square.defaultInstance(
                    s.getAxis(), p.getColor(s.getColorCode()), s.getColorCode()
            ));
            rh.add(Collections.unmodifiableList(rs));
        }
        return new TetrisLandscape(Collections.unmodifiableList(rh), landscape, score);
    }

    /**
     * A method to determine if a {@code Tetromino} has contacted the surface of the landscape.
     *
     * @param t the {@code Tetromino} to check for contact
     * @return whether or not the given {@code Tetromino} has contacted the landscape from above.
     */
    public boolean isImpactedBy(@NotNull final Tetromino t){
        for(final Square s: t.getBaseSquares())
            if (landscape.contains(new Point(s.getAxis().x, s.getAxis().y + Utility.SQUARE_LENGTH)))
                return true;
        return false;
    }

    /**
     * A method to move the given {@code Tetromino} to the left (if doing so doesn't result in impact).
     *
     * @param t the {@code Tetromino} to move
     * @return either a moved instance of {@code Tetromino} or the argument if unsuccessful
     */
    public Tetromino tryMovingLeft(@NotNull final Tetromino t){
        final Tetromino r = t.slide(Direction.LEFT);
        for(final Square s: r.getBaseSquares())
            if(landscape.contains(s.getAxis()) || s.getAxis().x <= LEFT_BOUNDARY)
                return t;
        return r;
    }

    /**
     * A method to move the given {@code Tetromino} to the right (if doing so doesn't result in impact).
     *
     * @param t the {@code Tetromino} to move
     * @return either a moved instance of {@code Tetromino} or the argument if unsuccessful
     */
    public Tetromino tryMovingRight(@NotNull final Tetromino t){
        final Tetromino r = t.slide(Direction.RIGHT);
        for(final Square s: r.getBaseSquares())
            if(landscape.contains(s.getAxis()) || s.getAxis().x >= RIGHT_BOUNDARY)
                return t;
        return r;
    }

    /**
     * A method to move the given {@code Tetromino} downwards (if doing so doesn't result in impact).
     *
     * @param t the {@code Tetromino} to move
     * @return either a moved instance of {@code Tetromino} or the argument if unsuccessful
     */
    public Tetromino tryFalling(@NotNull final Tetromino t){
        final Tetromino r = t.fall();
        for(final Square s: r.getBaseSquares())
            if(landscape.contains(s.getAxis()))
                return t;
        return r;
    }

    /**
     * A method to rotate the given {@code Tetromino}, if doing so doesn't result in impact.
     *
     * @param t the {@code Tetromino} to rotate
     * @return either a rotated {@code Tetromino} or the argument if rotation is unsuccessful
     */
    public Tetromino tryRotation(@NotNull final Tetromino t){
        final Tetromino r = t.rotate();
        for(final Square s: r.getBaseSquares()) {
            if (landscape.contains(s.getAxis()) || s.getAxis().x < 0
                    || s.getAxis().x >= Utility.GRID_WIDTH)
                return t;
        }
        return r;
    }

    /**
     * A method to determine if any {@code Square} in a given {@code Tetromino} lies at a {@code Point}
     * within the landscape.
     *
     * @param t the {@code Tetromino} to check
     * @return whether or not the given {@code Tetromino} lies at a {@code Point} within the landscape
     */
    public boolean contains(@NotNull final Tetromino t){
        for(final Square s: t.getBaseSquares())
            if(landscape.contains(s.getAxis())) return true;
        return false;
    }

    /**
     * Exposes the {@code Landscape}'s score.
     *
     * @return the {@code Landscape}'s score
     */
    public int getScore(){
        return score;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void paint(@NotNull final Graphics g){
        for(final List<Square> ls: history) {
            if(ls.isEmpty()) break;
            for(final Square s: ls) s.paint(g);
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(final List<Square> ls: history) {
            if(ls.isEmpty()) break;
            for(Square s: ls) sb.append(s).append("; ");
        }
        return sb.toString();
    }

}