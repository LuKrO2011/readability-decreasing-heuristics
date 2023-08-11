package de.uni_passau.fim.se2.rdh.util;

/**
 * This class provides a way to get a sequence of numbers for each instance of this class.
 */
public class NumberIterator {
    private int next = 0;

    /**
     * Returns the next number.
     *
     * @return the next number
     */
    public int getNext() {
        return next++;
    }

    /**
     * Resets the number iterator.
     */
    public void reset() {
        next = 0;
    }
}
