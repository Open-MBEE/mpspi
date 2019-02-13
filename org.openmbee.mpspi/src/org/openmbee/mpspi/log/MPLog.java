package org.openmbee.mpspi.log;

public class MPLog {
    public enum Level {
        ALL,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        NONE;
    }

    public interface Logger {
        Level getLevel(MPLog mpLog);
        void error(MPLog mpLog, String msg);
        void warn(MPLog mpLog, String msg);
        void info(MPLog mpLog, String msg);
        void debug(MPLog mpLog, String msg);
    }

    private static class DefaultLogger implements Logger {
        public Level level = Level.ALL;

        public Level getLevel(MPLog mpLog) {
            return level;
        }
        public void error(MPLog mpLog, String msg) {
            System.err.println(mpLog.getTargetClass() + ":" + msg);
        }
        public void warn(MPLog mpLog, String msg) {
            System.err.println(mpLog.getTargetClass() + ":" + msg);
        }
        public void info(MPLog mpLog, String msg) {
            System.err.println(mpLog.getTargetClass() + ":" + msg);
        }
        public void debug(MPLog mpLog, String msg) {
            System.err.println(mpLog.getTargetClass() + ":" + msg);
        }
    }

    private static Logger logger = new DefaultLogger();

    public static void setLogger(Logger l) {
        logger = l;
    }

    public static void setDefaultLevel(Level lv) {
        if (logger instanceof DefaultLogger) {
            DefaultLogger defaultLogger = (DefaultLogger) logger;
            defaultLogger.level = lv;
        }
    }

    public boolean check(Level lv) {
        Level llv = logger.getLevel(this);
        return (lv.ordinal() >= llv.ordinal());
    }

    private final Class<?> targetClass;
    public Class<?> getTargetClass() {
        return targetClass;
    }

    public MPLog(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public void error(String msg) {
        if (!check(Level.ERROR)) return;
        logger.error(this, msg);
    }

    public void warn(String msg) {
        if (!check(Level.WARN)) return;
        logger.warn(this, msg);
    }

    public void info(String msg) {
        if (!check(Level.INFO)) return;
        logger.info(this, msg);
    }

    public void debug(String msg) {
        if (!check(Level.DEBUG)) return;
        logger.debug(this, msg);
    }
}
