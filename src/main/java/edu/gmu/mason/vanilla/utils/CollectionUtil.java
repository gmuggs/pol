package edu.gmu.mason.vanilla.utils;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.RandomAccess;

import ec.util.MersenneTwisterFast;

/**
 * General description_________________________________________________________
 * A class to help collection tasks
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */

public class CollectionUtil {
	private static final int SHUFFLE_THRESHOLD        =    5;
	
	/**
	 * This method return a randomly chosen item from given collection. Random
	 * number is dynamically created using {@code MersenneTwisterFast} instance. It
	 * means, this method cannot create re-producable results. Use
	 * {@code CollectionUtil.getRandomItem (Collection<E> c, MersenneTwisterFast mt)}
	 * for controlling the pseudo-number generator.
	 * 
	 * @param c collection
	 * @return random item
	 */
	public static <E> Optional<E> getRandomItem (Collection<E> c) {
		MersenneTwisterFast mt = new MersenneTwisterFast();
	    return getRandomItem(c, mt);
	}
	
	/**
	 * This method return a randomly chosen item from given collection.
	 * @param c collection
	 * @param mt {@code MersenneTwisterFast} instance
	 * @return random item
	 */
	public static <E> Optional<E> getRandomItem (Collection<E> c, MersenneTwisterFast mt) {

	    return c.stream()
	            .skip((int) (c.size() * mt.nextDouble()))
	            .findFirst();
	}
	
	/**
	 * Safe way of shuffling lists. Taken from JDK8's shuffle implementation:
	 * https://hg.openjdk.java.net/jdk8/jdk8/jdk/file/687fd7c7986d/src/share/classes/java/util/Collections.java
	 * 
	 * @param list
	 *            the list to be shuffled
	 * @param mt
	 *            the source of randomness to use to shuffle the list.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void shuffle(List<?> list, MersenneTwisterFast mt) {
		int size = list.size();
		
		// Hamdi's note: I checked that .nextInt(int) method has the same boundaries (0: inclusive, n: exclusive)
        if (size < SHUFFLE_THRESHOLD || list instanceof RandomAccess) {
            for (int i=size; i>1; i--)
                swap(list, i-1, mt.nextInt(i));
        } else {
            Object arr[] = list.toArray();

            // Shuffle array
            for (int i=size; i>1; i--)
                swap(arr, i-1, mt.nextInt(i));

            // Dump array back into list
            // instead of using a raw type here, it's possible to capture
            // the wildcard but it will require a call to a supplementary
            // private method
            ListIterator it = list.listIterator();
            for (int i=0; i<arr.length; i++) {
                it.next();
                it.set(arr[i]);
            }
        }
    }
	
	

	// TAKEN FROM JDK 8 STARTS
    /**
     * Swaps the elements at the specified positions in the specified list.
     * (If the specified positions are equal, invoking this method leaves
     * the list unchanged.)
     *
     * @param list The list in which to swap elements.
     * @param i the index of one element to be swapped.
     * @param j the index of the other element to be swapped.
     * @throws IndexOutOfBoundsException if either <tt>i</tt> or <tt>j</tt>
     *         is out of range (i &lt; 0 || i &gt;= list.size()
     *         || j &lt; 0 || j &gt;= list.size()).
     * @since 1.4
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void swap(List<?> list, int i, int j) {
        // instead of using a raw type here, it's possible to capture
        // the wildcard but it will require a call to a supplementary
        // private method
        final List l = list;
        l.set(i, l.set(j, l.get(i)));
    }

    /**
     * Swaps the two specified elements in the specified array.
     */
    private static void swap(Object[] arr, int i, int j) {
        Object tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
	// TAKEN FROM JDK 8 ENDS
	
	/**
	 * Checks if a collection is null or empty.
	 * Taken from: https://stackoverflow.com/a/12721103
	 * @param c
	 * @return
	 */
	public static boolean isNullOrEmpty( final Collection< ? > c ) {
	    return c == null || c.isEmpty();
	}
	
	/**
	 * Checks if a map is null or empty.
	 * Taken from: https://stackoverflow.com/a/12721103
	 * @param c
	 * @return
	 */
	public static boolean isNullOrEmpty( final Map< ?, ? > m ) {
	    return m == null || m.isEmpty();
	}
}
