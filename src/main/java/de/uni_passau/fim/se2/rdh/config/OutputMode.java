package de.uni_passau.fim.se2.rdh.config;

/**
 * The output mode can be either into the new given output directory or into the input directory.
 */
public enum OutputMode {

    /**
     * If the output is written into a new directory (output dir).
     */
    NEW_DIRECTORY,
    /**
     * If the output is written into the input directory.
     */
    SAME_DIRECTORY,

    /**
     * If the output is written into a new directory (output dir), but the subdirectory structure is kept.
     */
    STRUCTURE_KEEPING
}
