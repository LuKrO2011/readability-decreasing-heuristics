package de.uni_passau.fim.se2.rdh.util;

import java.util.Random;

/**
 * Wrapper for {@link Random} to allow for reproducible runs using the same seed.
 */
public final class Randomness {

    private static final Random RANDOM = new Random();

    private Randomness() {
        throw new IllegalCallerException("utility class");
    }

    /**
     * Setting a seed for the random parameter.
     *
     * @param seed The given seed.
     */
    public static void setSeed(int seed) {
        RANDOM.setSeed(seed);
    }

    /**
     * Returns a integer uniformly.
     *
     * @param bound The upper bound.
     * @return A uniformly and random created int with upper bound.
     */
    public static int nextInt(int bound) {
        return RANDOM.nextInt(bound);
    }

    /**
     * Creates a random string in lower cases.
     *
     * @param limit The maximal limit of the length.
     * @return A random string in lower cases.
     */
    public static String randomString(int limit) {
        final int lowerBound = 'a';
        final int upperBound = 'z';

        return RANDOM.ints(lowerBound, upperBound + 1)
            .limit(limit)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }
}
