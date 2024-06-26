package de.uni_passau.fim.se2.rdh.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

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
    protected void attachAppender(Class<?> clazz) {
        logger = (Logger) LoggerFactory.getLogger(clazz);

        // Create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // Add the appender to the logger
        logger.addAppender(listAppender);

        // Remember the appender to detach it later
        log = listAppender;
    }

    /**
     * Detaches an appender from the logger.
     *
     * @param appender The appender to detach.
     */
    private void detachAppender(ListAppender<ILoggingEvent> appender) {
        logger.detachAppender(appender);
    }

    /**
     * Asserts that the log does not contain any warnings or errors.
     */
    protected void assertLogIsEmpty() {
        assertThat(log.list).extracting(ILoggingEvent::getLevel)
                .noneMatch(level -> level == ch.qos.logback.classic.Level.WARN
                        || level == ch.qos.logback.classic.Level.ERROR);
    }

    /**
     * Asserts that the log contains exactly the given messages.
     *
     * @param messages The messages to check for.
     */
    protected void assertLogContainsExactly(String... messages) {
        assertThat(log.list).extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly(messages);
    }

    /**
     * Asserts that the log contains the given messages.
     *
     * @param messages The messages to check for.
     */
    protected void assertLogContains(String... messages) {
        assertThat(log.list).extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(message -> {
                    for (String m : messages) {
                        if (message.contains(m)) {
                            return true;
                        }
                    }
                    return false;
                });
    }
}
