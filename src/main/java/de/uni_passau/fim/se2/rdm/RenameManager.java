package de.uni_passau.fim.se2.rdm;


/**
 * This class manages the steps for renaming a variable in a Java program.
 */
public class RenameManager {
    public static void main(String[] args) {
        new VariableRenamer()
                .readClasses()
                .renameVariable()
                .renameMethod()
                .writeTransformedClasses();
    }
}
