package de.uni_passau.fim.se2.rdh.refactorings.rename.realistic;

public enum NameSelectionMode {

    /**
     * Selects the name with a certain quality (given in a config file or hardcoded).
     */
    QUALITY,

    /**
     * Selects the longest name.
     */
    LONGEST
}
