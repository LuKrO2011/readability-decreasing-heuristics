package de.uni_passau.fim.se2.rdh.util;

import org.slf4j.Logger;

/**
 * This class contains reusable logging methods.
 * <p>
 * This makes it easy to change the severity level and
 * appended stack trace information for certain kind of log messages.
 */
public class Logging {

    /**
     * Logs a refactoring failure.
     *
     * @param logger  The logger to use
     * @param message The message to log
     * @param e       The exception to log
     */
    public static void logRefactoringFailed(Logger logger, String message, Exception e) {
        if (logger.isWarnEnabled()) {
            logger.warn(message, e);
        }
    }
}
