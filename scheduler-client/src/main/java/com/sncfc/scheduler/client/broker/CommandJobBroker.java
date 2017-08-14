package com.sncfc.scheduler.client.broker;

import com.sncfc.scheduler.client.core.JobBroker;
import com.sncfc.scheduler.client.core.JobExecutionContext;
import com.sncfc.scheduler.client.core.JobExecutionException;
import com.sncfc.scheduler.client.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.logging.Level;


public class CommandJobBroker implements JobBroker {

    public void execute(ServletContext servletContext, JobExecutionContext jobContext) throws JobExecutionException {
        String cl = (String) jobContext.getData("commandLine");
        if (StringUtils.isEmpty(cl)) {
            throw new JobExecutionException("Can not find commandLine in jobExecutionContext data.");
        }
        String[] arguments = (String[]) jobContext.getData("arguments");
        Integer exitValue = (Integer) jobContext.getData("exitValue");
        Long timeout = (Long) jobContext.getData("timeout");
        boolean async = (Boolean) jobContext.getData("async");
        try {
            Class commandLineClass = Class.forName("org.apache.commons.exec.CommandLine");
            Class defaultExecutorClass = Class.forName("org.apache.commons.exec.DefaultExecutor");
            Class executeWatchdogClass = Class.forName("org.apache.commons.exec.ExecuteWatchdog");
            Class executeStreamHandlerClass = Class.forName("org.apache.commons.exec.ExecuteStreamHandler");
            Class pumpStreamHandlerClass = Class.forName("org.apache.commons.exec.PumpStreamHandler");
            Class executeResultHandlerClass = Class.forName("org.apache.commons.exec.ExecuteResultHandler");
            Class defaultExecuteResultHandlerClass = Class
                    .forName("org.apache.commons.exec.DefaultExecuteResultHandler");

            Method parseMethod = commandLineClass.getMethod("parse", String.class);
            Object commandLine = parseMethod.invoke(null, cl);
            if (arguments != null && arguments.length != 0) {
                Method addArgumentsMethod = commandLineClass.getMethod("addArguments", String[].class);
                addArgumentsMethod.invoke(commandLine, new Object[] { arguments });
            }

            Object executor = defaultExecutorClass.newInstance();
            if (exitValue != null) {
                Method setExitValueMethod = defaultExecutorClass.getMethod("setExitValue", int.class);
                setExitValueMethod.invoke(executor, exitValue);
            } else {
                Method setExitValuesMethod = defaultExecutorClass.getMethod("setExitValues", int[].class);
                setExitValuesMethod.invoke(executor, new Object[] { null });
            }

            if (timeout != null) {
                Constructor executeWatchdogConstructor = executeWatchdogClass.getConstructor(long.class);
                Object executeWatchdog = executeWatchdogConstructor.newInstance(timeout);
                Method setWatchdogMethod = defaultExecutorClass.getMethod("setWatchdog", executeWatchdogClass);
                setWatchdogMethod.invoke(executor, executeWatchdog);
            }

            Object outStream = new LogOutputStream(Level.INFO);
            Object errStream = new LogOutputStream(Level.SEVERE);
            Constructor pumpStreamHandlerConstructor = pumpStreamHandlerClass.getConstructor(new Class[] {
                    OutputStream.class, OutputStream.class });
            Object pumpStreamHandler = pumpStreamHandlerConstructor.newInstance(outStream, errStream);
            Method setStreamHandlerMethod = defaultExecutorClass.getMethod("setStreamHandler",
                    executeStreamHandlerClass);
            setStreamHandlerMethod.invoke(executor, pumpStreamHandler);

            if (async) {
                Object resultHandler = defaultExecuteResultHandlerClass.newInstance();
                Method executeMethod = defaultExecutorClass.getMethod("execute", new Class[] { commandLineClass,
                        executeResultHandlerClass });
                executeMethod.invoke(executor, commandLine, resultHandler);
            } else {
                Method executeMethod = defaultExecutorClass.getMethod("execute", new Class[] { commandLineClass });
                executeMethod.invoke(executor, commandLine);
            }
        } catch (ClassNotFoundException ex) {
            throw new JobExecutionException("Can not load dependency, "
                    + "please put commons-exec-1.1.jar in your classPath.", ex);
        } catch (Exception ex) {
            throw new JobExecutionException(ex);
        }
    }

    public class LogOutputStream extends OutputStream {

        private final Logger logger = LoggerFactory.getLogger(CommandJobBroker.class.getName());

        /**
         * Initial buffer size.
         */
        private static final int INTIAL_SIZE = 132;

        /**
         * Carriage return
         */
        private static final int CR = 0x0d;

        /**
         * Linefeed
         */
        private static final int LF = 0x0a;

        /**
         * the internal buffer
         */
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(INTIAL_SIZE);

        private boolean skip = false;

        private final Level level;

        /**
         * Creates a new instance of this class.
         * 
         * @param level logLevel used to log data written to this stream.
         */
        public LogOutputStream(final Level level) {
            this.level = level;
        }

        /**
         * Write the data to the buffer and flush the buffer, if a line separator is detected.
         * 
         * @param cc data to log (byte).
         * @see OutputStream#write(int)
         */
        public void write(final int cc) throws IOException {
            final byte c = (byte) cc;
            if ((c == '\n') || (c == '\r')) {
                if (!skip) {
                    processBuffer();
                }
            } else {
                buffer.write(cc);
            }
            skip = (c == '\r');
        }

        /**
         * Flush this log stream.
         *
         * @see OutputStream#flush()
         */
        public void flush() {
            if (buffer.size() > 0) {
                processBuffer();
            }
        }

        /**
         * Writes all remaining data from the buffer.
         *
         * @see OutputStream#close()
         */
        public void close() throws IOException {
            if (buffer.size() > 0) {
                processBuffer();
            }
            super.close();
        }

        /**
         * Write a block of characters to the output stream
         *
         * @param b the array containing the data
         * @param off the offset into the array where data starts
         * @param len the length of block
         * @throws IOException if the data cannot be written into the stream.
         * @see OutputStream#write(byte[], int, int)
         */
        public void write(final byte[] b, final int off, final int len) throws IOException {
            // find the line breaks and pass other chars through in blocks
            int offset = off;
            int blockStartOffset = offset;
            int remaining = len;
            while (remaining > 0) {
                while (remaining > 0 && b[offset] != LF && b[offset] != CR) {
                    offset++;
                    remaining--;
                }
                // either end of buffer or a line separator char
                int blockLength = offset - blockStartOffset;
                if (blockLength > 0) {
                    buffer.write(b, blockStartOffset, blockLength);
                }
                while (remaining > 0 && (b[offset] == LF || b[offset] == CR)) {
                    write(b[offset]);
                    offset++;
                    remaining--;
                }
                blockStartOffset = offset;
            }
        }

        /**
         * Converts the buffer to a string and sends it to <code>processLine</code>.
         */
        protected void processBuffer() {
            processLine(buffer.toString());
            buffer.reset();
        }

        /**
         * Logs a line to the log system of the user.
         * 
         * @param line the line to log.
         */
        protected void processLine(final String line) {
            if (Level.SEVERE.equals(level)) {
                logger.error(line);
            } else if (Level.WARNING.equals(level)) {
                logger.warn(line);
            } else {
                logger.info(line);
            }
        }
    }

}
