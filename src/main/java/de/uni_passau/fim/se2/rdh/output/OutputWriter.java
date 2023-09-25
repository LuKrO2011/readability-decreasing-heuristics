package de.uni_passau.fim.se2.rdh.output;

/**
 * Interface for output writers.
 */
public interface OutputWriter {

    /**
     * Writes the output. Therefore, the modified spoon pretty printer is used.
     *
     * @param inputs the inputs to write the output for
     */
    void writeOutput(String... inputs);
}
