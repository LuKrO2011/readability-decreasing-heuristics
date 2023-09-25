package de.uni_passau.fim.se2.rdh.output;

import spoon.SpoonAPI;

/**
 * Writes the output to a new folder.
 */
public class NewFolderOutputWriter implements OutputWriter {

    private final SpoonAPI spoon;

    /**
     * Creates a new SameFolderOutputWriter.
     *
     * @param spoon the spoon instance to use
     */
    public NewFolderOutputWriter(final SpoonAPI spoon) {
        this.spoon = spoon;
    }

    /**
     * Create the output files (java code) using the modified spoon pretty printer. The output is written to the given
     * directory. The subdirectory and filename depend on the package and the class name.
     *
     * @param inputs Ignored
     */
    @Override
    public void writeOutput(final String... inputs) {
        spoon.prettyprint();
    }

}
