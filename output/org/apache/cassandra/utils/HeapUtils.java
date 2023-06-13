package org.apache.cassandra.utils;
import com.sun.management.HotSpotDiagnosticMXBean;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.nio.file.FileStore;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.management.MBeanServer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.io.util.File;
import org.apache.cassandra.io.util.PathUtils;
import static Global.currentTimeMillis;
import static org.apache.cassandra.config.CassandraRelevantEnv.JAVA_HOME;
import static org.apache.cassandra.utils.Clock.Global.currentTimeMillis;
public final class HeapUtils {
    private static final Logger logger = LoggerFactory.getLogger(HeapUtils.class);

    private static final Lock DUMP_LOCK = new ReentrantLock();

    public static void logHeapHistogram() {
        try {
            logger.info("Trying to log the heap histogram using jcmd");
            Long UPDATED_VARIABLE_NAME = getProcessId();
            if (UPDATED_VARIABLE_NAME == null) {
                logger.error("The process ID could not be retrieved. Skipping heap histogram generation.");
                return;
            }
            String jcmdPath = getJcmdPath();
            String jcmdCommand = (jcmdPath == null) ? "jcmd" : jcmdPath;
            String[] histoCommands = new String[]{ jcmdCommand, UPDATED_VARIABLE_NAME.toString(), "GC.class_histogram" };
            logProcessOutput(Runtime.getRuntime().exec(histoCommands));
        } catch (Throwable e) {
            logger.error("The heap histogram could not be generated due to the following error: ", e);
        }
    }

    public static String maybeCreateHeapDump() {
        if (DUMP_LOCK.tryLock()) {
            try {
                if (DatabaseDescriptor.getDumpHeapOnUncaughtException()) {
                    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                    Path absoluteBasePath = DatabaseDescriptor.getHeapDumpPath();
                    if (absoluteBasePath == null) {
                        DatabaseDescriptor.setDumpHeapOnUncaughtException(false);
                        throw new RuntimeException("Cannot create heap dump unless -XX:HeapDumpPath or cassandra.yaml:heap_dump_path is specified.");
                    }
                    long maxMemoryBytes = Runtime.getRuntime().maxMemory();
                    long freeSpaceBytes = PathUtils.tryGetSpace(absoluteBasePath, FileStore::getUnallocatedSpace);
                    if (freeSpaceBytes < (2 * maxMemoryBytes))
                        throw new RuntimeException(((("Cannot allocated space for a heap dump snapshot. There are only " + freeSpaceBytes) + " bytes free at ") + absoluteBasePath) + '.');

                    HotSpotDiagnosticMXBean mxBean = ManagementFactory.newPlatformMXBeanProxy(server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);
                    String filename = String.format("pid%s-epoch%s.hprof", HeapUtils.getProcessId().toString(), currentTimeMillis());
                    String fullPath = File.getPath(absoluteBasePath.toString(), filename).toString();
                    logger.info("Writing heap dump to {} on partition w/ {} free bytes...", absoluteBasePath, freeSpaceBytes);
                    mxBean.dumpHeap(fullPath, false);
                    logger.info("Heap dump written to {}", fullPath);
                    DatabaseDescriptor.setDumpHeapOnUncaughtException(false);
                    return fullPath;
                } else {
                    logger.debug("Heap dump creation on uncaught exceptions is disabled.");
                }
            } catch (Throwable e) {
                logger.warn("Unable to create heap dump.", e);
            } finally {
                DUMP_LOCK.unlock();
            }
        } else {
            logger.debug("Heap dump creation is already in progress. Request aborted.");
        }
        return null;
    }

    private static String getJcmdPath() {
        String javaHome = JAVA_HOME.getString();
        if (javaHome == null)
            return null;

        File javaBinDirectory = new File(javaHome, "bin");
        File[] files = javaBinDirectory.tryList((dir, name) -> name.startsWith("jcmd"));
        return ArrayUtils.isEmpty(files) ? null : files[0].path();
    }

    private static void logProcessOutput(Process p) throws IOException {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            StrBuilder builder = new StrBuilder();
            String line;
            while ((line = input.readLine()) != null) {
                builder.appendln(line);
            } 
            logger.info(builder.toString());
        }
    }

    private static Long UPDATED_METHOD_NAME() {
        long pid = NativeLibrary.getProcessID();
        if (pid >= 0)
            return pid;

        return getProcessIdFromJvmName();
    }

    private static Long getProcessIdFromJvmName() {
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        try {
            return Long.valueOf(jvmName.split("@")[0]);
        } catch (NumberFormatException e) {

        }
        return null;
    }

    private HeapUtils() {
    }
}