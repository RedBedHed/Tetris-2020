package Engine.Navigation;

import Engine.Tetromino.Tetromino;
import Engine.Tetromino.Tetromino.TetrominoFactory;
import Engine.Tetromino.Square;
import Engine.Utility;
import GUI.Paintable;
import GUI.Palette;

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

    /** Constants */
    private static final int LANDSCAPE_INITIAL_SIZE = 200;
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
    private final List<List<Square>> history;

    /**
     * A {@code Set} of all {@code Point}s belonging to the landscape for use in
     * impact detection.
     */
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
    private TetrisLandscape(final List<List<Square>> history,
                            final Set<Point> landscape,
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
     * A static factory method to instantiate an updated {@code TetrisLandscape} object
     * using the previous {@code TetrisLandscape}. This method handles row-clearing with
     * an O(N) algorithm where N <= ~(400 + 10 + 4). The algorithm makes two passes over
     * the history {@code List} (once over the previous history and once over the current
     * history), and ensures that all updated data is unmodifiable prior to instantiation.
     * This algorithm sacrifices efficiency for the immutability of the TetrisLandscape.
     *
     * <p>
     * This method also handles scoring with an O(N) algorithm where N is the current level.
     * Scores are calculated using the original Nintendo scoring system:
     * <ol>
     *     <li> 1 line cleared = 40 * (current level) </li>
     *     <li> 2 lines cleared = 100 * (current level) </li>
     *     <li> 3 lines cleared = 300 * (current level) </li>
     *     <li> 4 lines cleared = 1200 * (current level) </li>
     * </ol>
     *
     * @param pl the {@code TetrisLandscape} object
     * @param t the current {@code Tetromino} to be added
     * @param height the number of squares the current {@code Tetromino} has travelled
     * @param level the current level number
     * @return an updated {@code TetrisLandscape} object
     */
    public static TetrisLandscape refresh(final TetrisLandscape pl,
                                          final Tetromino t,
                                          final int height,
                                          final int level) {

        /*
         * Initialize a mutable copy of an immutable two-dimensional "history"
         * List with four empty Lists added as a buffer. Four is the maximum number of
         * blocks that a Tetromino can contribute to the landscape in the vertical
         * direction.
         */
        final int MAXIMUM_TET_HEIGHT = 4;
        final List<List<Square>> rh = new ArrayList<>(HISTORY_INITIAL_SIZE);
        for(final List<Square> ls: pl.history) {
            if(ls.isEmpty()) break; rh.add(new ArrayList<>(ls));
        }
        for(int j = 0; j < MAXIMUM_TET_HEIGHT; j++) rh.add(new ArrayList<>());

        /*
         * Add the current Tetromino's Squares to the replacement history.
         */
        for(final Square s: t.getBaseSquares()) {
            final int r = ((s.getAxis().y < 0) || (s.getAxis().y >= Utility.GRID_HEIGHT)) ?
                    HISTORY_INITIAL_SIZE: (HISTORY_INITIAL_SIZE - 1) -
                    (s.getAxis().y >>> Utility.LOG_2_SQUARE_LENGTH);
            if(r < rh.size()) rh.get(r).add(s);
        }

        /*
         * Initialize the bottom row of the replacement landscape.
         */
        final Set<Point> rl = new HashSet<>(LANDSCAPE_INITIAL_SIZE);
        rl.addAll(INITIAL_POINTS_CACHE);

        /*
         * Omit any history row with 10 Squares or more. Shift down.
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
                rr.add(new Square(p, s.getColor(), s.getColorCode()));
                rl.add(p);
            }
            rh.set(i, Collections.unmodifiableList(rr));
        }

        /*
         * Score the current landscape.
         */
        int score = height + pl.score;
        switch(lineCount){
            case 4: for(int k = 0; k <= level; k++) score += 1200; break;
            case 3: for(int k = 0; k <= level; k++)  score += 300; break;
            case 2: for(int k = 0; k <= level; k++)  score += 100; break;
            case 1: for(int k = 0; k <= level; k++)   score += 40; break;
        }

        /*
         * Initialize.
         */
        return new TetrisLandscape(
                Collections.unmodifiableList(rh),
                Collections.unmodifiableSet(rl),
                score
        );

    }

    /**
     * A factory method to copy a {@code TetrisLandscape object}, re-coloring it in the process.
     *
     * @param pl the previous {@code TetrisLandscape}
     * @param p the palette to use in re-coloring
     * @return a re-colored instance of {@code TetrisLandscape}
     */
    public static TetrisLandscape reColor(final TetrisLandscape pl, final Palette p){
        final List<List<Square>> rh = new ArrayList<>();
        for(List<Square> ls: pl.history) {
            if(ls.isEmpty()) break;
            final List<Square> rs = new ArrayList<>();
            for(Square s: ls) rs.add(new Square(
                    s.getAxis(), p.getColor(s.getColorCode()), s.getColorCode()
            ));
            rh.add(Collections.unmodifiableList(rs));
        }
        return new TetrisLandscape(Collections.unmodifiableList(rh), pl.landscape, pl.score);
    }


    /**
     * A method to determine if a {@code Tetromino} has contacted the surface of the landscape.
     *
     * @param t the {@code Tetromino} to check for contact
     * @return whether or not the given {@code Tetromino} has contacted the landscape from above.
     */
    public boolean imminentImpact(final Tetromino t){
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
    public Tetromino tryMovingLeft(final Tetromino t){
        final Tetromino r = TetrominoFactory.slidingInstance(t, Direction.LEFT);
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
    public Tetromino tryMovingRight(final Tetromino t){
        final Tetromino r = TetrominoFactory.slidingInstance(t, Direction.RIGHT);
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
    public Tetromino tryFalling(final Tetromino t){
        final Tetromino r = TetrominoFactory.fallingInstance(t);
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
    public Tetromino tryRotation(final Tetromino t){
        final Tetromino r = TetrominoFactory.rotatingInstance(t);
        for(final Square s: r.getBaseSquares()) {
            if (landscape.contains(s.getAxis()) || s.getAxis().x < 0 || s.getAxis().x >= Utility.GRID_WIDTH)
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
    public boolean contains(final Tetromino t){
        for(final Square s: t.getBaseSquares()) if(landscape.contains(s.getAxis())) return true;
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
    public void paint(Graphics g){
        for(List<Square> ls: history) {
            if(ls.isEmpty()) break;
            for(final Square s: ls) {
                s.paint(g);
            }
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
