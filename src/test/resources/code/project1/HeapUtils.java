/**
 * Utility to log heap histogram.
 */
public
final class
HeapUtils  {
        private static
        final org.slf4j.Logger  f0
        =  org.slf4j.LoggerFactory.getLogger(org.apache.cassandra.utils.HeapUtils.class);
         private  static final java.util.concurrent.locks.Lock  DUMP_LOCK  = new  java.util.concurrent.locks.ReentrantLock();
        /**
         * Generates a HEAP histogram in the log file.
         */
        public
        static void
        m0()
        {
            try {org.apache.cassandra.utils.HeapUtils.f0.info("Trying to log the heap histogram using jcmd");
                java.lang.Long processId =  org.apache.cassandra.utils.HeapUtils.getProcessId();
                if (processId == null) {org.apache.cassandra.utils.HeapUtils.f0.error("The process ID could not be retrieved. Skipping heap histogram generation.");
                return;
        }java.lang.String  jcmdPath = org.apache.cassandra.utils.HeapUtils.getJcmdPath();
        // The jcmd file could not be found. In this case let's default to jcmd in the hope that it is in the path.
        java.lang.String
        jcmdCommand =
        (jcmdPath == null) ? "jcmd"
        : jcmdPath; java.lang.String[] histoCommands = new java.lang.String[]{ jcmdCommand, processId.toString(), "GC.class_histogram" }; org.apache.cassandra.utils.HeapUtils.logProcessOutput(java.lang.Runtime.getRuntime().exec(histoCommands)); } catch (java.lang.Throwable e)  {

org.apache.cassandra.utils.HeapUtils.f0.error("The heap histogram could not be generated due to the following error: ", e);
}
} 

public static
java.lang.String maybeCreateHeapDump() {// Make sure that only one heap dump can be in progress across all threads, and abort for

// threads that cannot immediately acquire the lock, allowing them to fail normally.
if (org.apache.cassandra.utils.HeapUtils.DUMP_LOCK.tryLock()) {try  {
        if
        (org.apache.cassandra.config.DatabaseDescriptor.getDumpHeapOnUncaughtException()) {
            javax.management.MBeanServer
            server = java.lang.management.ManagementFactory.getPlatformMBeanServer();
            java.nio.file.Path absoluteBasePath = org.apache.cassandra.config.DatabaseDescriptor.getHeapDumpPath();
            // the -XX param / .yaml conf is present on initial init and the JMX entry point, but still worth checking.
            if (absoluteBasePath
            == null)  {

                org.apache.cassandra.config.DatabaseDescriptor.setDumpHeapOnUncaughtException(false);

                throw new  java.lang.RuntimeException("Cannot create heap dump unless -XX:HeapDumpPath or cassandra.yaml:heap_dump_path is specified."); }
        long maxMemoryBytes = java.lang.Runtime.getRuntime().maxMemory(); long freeSpaceBytes  =  org.apache.cassandra.io.util.PathUtils.tryGetSpace(absoluteBasePath,  java.nio.file.FileStore::getUnallocatedSpace); 

        if (freeSpaceBytes
        < (2 * maxMemoryBytes)) throw
            new java.lang.RuntimeException(((("Cannot allocated space for a heap dump snapshot. There are only "  + freeSpaceBytes) + " bytes free at ") +
            absoluteBasePath) + '.');
         com.sun.management.HotSpotDiagnosticMXBean mxBean
        = java.lang.management.ManagementFactory.newPlatformMXBeanProxy(server, "com.sun.management:type=HotSpotDiagnostic", com.sun.management.HotSpotDiagnosticMXBean.class); java.lang.String v9 = java.lang.String.format("pid%s-epoch%s.hprof", org.apache.cassandra.utils.HeapUtils.getProcessId().toString(), Global.currentTimeMillis());java.lang.String fullPath = org.apache.cassandra.io.util.File.getPath(absoluteBasePath.toString(), v9).toString();
        org.apache.cassandra.utils.HeapUtils.f0.info("Writing heap dump to {} on partition w/ {} free bytes...", absoluteBasePath, freeSpaceBytes);
        mxBean.dumpHeap(fullPath,
        false);

        org.apache.cassandra.utils.HeapUtils.f0.info("Heap dump written to {}",
        fullPath);
        org.apache.cassandra.config.DatabaseDescriptor.setDumpHeapOnUncaughtException(false); return
        fullPath; } else
    {
        org.apache.cassandra.utils.HeapUtils.f0.debug("Heap dump creation on uncaught exceptions is disabled.");
}
} catch (java.lang.Throwable e) {
org.apache.cassandra.utils.HeapUtils.f0.warn("Unable to create heap dump.", e);
}
finally {
    org.apache.cassandra.utils.HeapUtils.DUMP_LOCK.unlock();
}} else {
org.apache.cassandra.utils.HeapUtils.f0.debug("Heap dump creation is already in progress. Request aborted.");}
return null;
}

private  static java.lang.String
getJcmdPath()
{ // Searching in the JAVA_HOME is safer than searching into System.getProperty("java.home") as the Oracle

java.lang.String v11 = CassandraRelevantEnv.JAVA_HOME.getString();
if (v11 ==
null) return null; 
org.apache.cassandra.io.util.File v12  =  new
org.apache.cassandra.io.util.File(v11, "bin");org.apache.cassandra.io.util.File[] files = v12.tryList(( dir,  name) ->
name.startsWith("jcmd"));return  org.apache.commons.lang3.ArrayUtils.isEmpty(files) ? null : files[0].path();
}
 /**
 * Logs the output of the specified process.
 *
 * @param p
 * 		the process
 * @throws IOException
 * 		if an I/O problem occurs
 */

private static void logProcessOutput(java.lang.Process p) throws  java.io.IOException  {try  (java.io.BufferedReader input = new  java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
org.apache.commons.lang3.text.StrBuilder builder =
new
org.apache.commons.lang3.text.StrBuilder();java.lang.String line;
while ((line = input.readLine()) !=
null) {builder.appendln(line); } 
org.apache.cassandra.utils.HeapUtils.f0.info(builder.toString());
} } 
private
static
java.lang.Long getProcessId() {
long pid = org.apache.cassandra.utils.NativeLibrary.getProcessID(); if (pid >= 0)
return pid;
 return org.apache.cassandra.utils.HeapUtils.m1();
}


private static  java.lang.Long
m1()  { 
java.lang.String
jvmName =  java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
try {
return
java.lang.Long.valueOf(jvmName.split("@")[0]);

} catch (java.lang.NumberFormatException e) {


} return
null;} /**
 * The class must not be instantiated.
 */
private HeapUtils()
{
} }