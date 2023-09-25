package de.uni_passau.fim.se2.rdh.output;

import spoon.SpoonAPI;

public class NewFolderOutputWriter extends AbstractOutputWriter {

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
     * Create the output files (java code) using the modified spoon pretty printer.
     */
    @Override
    public void writeOutput() {
        spoon.prettyprint();
    }

}
