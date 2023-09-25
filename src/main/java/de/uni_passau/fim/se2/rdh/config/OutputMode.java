package de.uni_passau.fim.se2.rdh.config;

/**
 * The output mode can be used to specify in which folder structure the output should be written.
 */
public enum OutputMode {

    /**
     * If the output is written into a new directory (output dir) and the subdirectory structure is not kept.
     */
    UNSTRUCTURED,
    /**
     * If each output file is written into the same directory  as the input file (input dir).
     */
    SAME_DIRECTORY,

    /**
     * If the output is written into a new directory (output dir) and the subdirectory structure is taken from the input
     * directory.
     */
    STRUCTURED
}
