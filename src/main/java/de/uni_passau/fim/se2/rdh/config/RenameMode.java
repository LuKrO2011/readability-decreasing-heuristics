package de.uni_passau.fim.se2.rdh.config;

/**
 * The renaming mode of the tool can be either iterative or reasonable.
 */
public enum RenameMode {
    /**
     * If the renaming is done iteratively using {@link de.uni_passau.fim.se2.rdh.util.NumberIterator}.
     */
    ITERATIVE,
    /**
     * If the renaming is done using a machine learning model.
     */
    REASONABLE
}
