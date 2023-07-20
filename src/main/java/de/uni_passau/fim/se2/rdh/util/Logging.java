package de.uni_passau.fim.se2.rdh.util;

import org.slf4j.Logger;

/**
 * This class contains reusable logging methods.
 * <p>
 * This makes it easy to change the severity level and appended stack trace information for certain kind of log
 * messages.
 */
public final class Logging {

    private Logging() {
    }

    /**
     * Logs a refactoring failure.
     *
     * @param logger  The logger to use
     * @param message The message to log
     * @param e       The exception to log
     */
    public static void logRefactoringFailed(final Logger logger, final String message, final Exception e) {
        if (logger.isWarnEnabled()) {
            logger.warn(message, e);
        }
    }
}
