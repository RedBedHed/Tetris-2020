package Engine;

import java.util.*;

/**
 * Utility
 *
 * <p>
 * A class containing static methods and constants to be used
 * throughout the {@code Engine} package.
 */
public final class Utility {

    /** No touchie~! */
    private Utility(){
    }

    /** Constants */
    public static final int SQUARE_LENGTH        =  32;
    public static final int LOG_2_SQUARE_LENGTH  =   5;
    public static final int DOUBLE_SQUARE_LENGTH =  64;
    public static final int HALF_SQUARE_LENGTH   =  16;
    public static final int SQUARE_BUFFER        =   2;
    public static final int OUTER_SQUARE_LENGTH  =  30;
    public static final int INNER_SQUARE_LENGTH  =  28;
    public static final int GRID_WIDTH           = 320;
    public static final int GRID_HEIGHT          = 640;
    public static final Random rgen = new Random();
    public static final String IMAGES_PATH;
    static {
        IMAGES_PATH = "C:/Users/evcmo/IdeaProjects/Tetris/art/";
    }

    /**
     * A method to return a synchronized, unmodifiable version of the given
     * {@code List}.
     *
     * @param list the {@code List} to be "locked".
     * @param <T> the type
     * @return a synchronized, unmodifiable {@code List}
     */
    public static <T> List<T> lockedList(final List<T> list){
        return Collections.synchronizedList(Collections.unmodifiableList(list));
    }

    /**
     * A method to shuffle the given array and return a synchronized, unmodifiable
     * {@code List} of its elements.
     *
     * @param a the array to be shuffled
     * @param <T> the type
     * @return a synchronized and unmodifiable {@code List}
     */
    public static <T> List<T> shuffle(final T[] a){
        for(int i = 0; i < a.length; i++){
            final int r = Utility.rgen.nextInt(7);
            final T t = a[i];
            a[i] = a[r];
            a[r] = t;
        }
        return Collections.synchronizedList(Arrays.asList(a));
    }

    /**
     * A method to count the positive digits in a given integer.
     *
     * @param i an integer
     * @return the number of positive digits contained in the argument
     */
    public static int countPositiveDigits(int i){
        if(i == 0) return 1;
        int count = 0;
        while(i > 0) {i /= 10; count++;}
        return count;
    }

    /**
     * Concise Array List
     *
     * @param <E> the element type
     * @see java.util.ArrayList;
     */
    public static final class ConciseArrayList<E> extends ArrayList<E> {

        /**
         * A private constructor for a {@code ConciseArrayList}
         */
        public ConciseArrayList(){
            super();
        }

        /**
         * An "add" method that supports chaining.
         *
         * @param element the element to be added
         * @return the instance
         */
        public ConciseArrayList<E> with(final E element){
            add(element);
            return this;
        }

    }

}
