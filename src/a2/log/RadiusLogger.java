package a2.log;

/**
 * JRadius RadiusLogger Interface.
 * @author David Bird
 */
public interface RadiusLogger
{
    public final int LEVEL_OFF = 0;
    public final int LEVEL_ERROR = 2;
    public final int LEVEL_WARNING = 4;
    public final int LEVEL_INFO = 6;
    public final int LEVEL_DEBUG = 8;

    public abstract boolean isLoggable(int logLevel);

    public abstract void error(String message);
    public abstract void error(String message, Throwable e);
    public abstract void warn(String message);
    public abstract void warn(String message, Throwable e);
    public abstract void info(String message);
    public abstract void info(String message, Throwable e);
    public abstract void debug(String message);
    public abstract void debug(String message, Throwable e);
}
