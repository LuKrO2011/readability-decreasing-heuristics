package de.uni_passau.fim.se2.rdh.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.slf4j.LoggerFactory;

/**
 * This class enables testing on logger output.
 */
public class LoggerTest {

    /**
     * The logger.
     */
    protected Logger logger;

    /**
     * The log.
     */
    protected ListAppender<ILoggingEvent> log;

    /**
     * Detaches the appender from the logger if it was attached after each test.
     */
    @AfterEach
    protected void tearDown() {
        if (log != null) {
            detachAppender(log);
        }
    }

    /**
     * Attaches an appender to the logger to capture log output.
     *
     * @param clazz The class to attach the appender to.
     * @return The attached appender which can be used to assert log output.
     */
    protected ListAppender<ILoggingEvent> attachAppender(Class<?> clazz) {
        logger = (Logger) LoggerFactory.getLogger(clazz);

        // Create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // Add the appender to the logger
        logger.addAppender(listAppender);

        return listAppender;
    }

    /**
     * Detaches an appender from the logger.
     *
     * @param appender The appender to detach.
     */
    private void detachAppender(ListAppender<ILoggingEvent> appender) {
        logger.detachAppender(appender);
    }
}
