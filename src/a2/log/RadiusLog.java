package a2.log;

/**
 * JRadius Server Logger Class.
 * @author David Bird
 */
public final class RadiusLog 
{
    public static boolean isLoggable(int logLevel)
    {
        RadiusLogger ler = RadiusLog.logger;

        if (ler != null)
        {
            return ler.isLoggable(logLevel);
        }

        return false;
    }

    public static void error(String message)
    {
        RadiusLogger ler = RadiusLog.logger;

        if (ler != null)
        {
            ler.error(message);
        }
    }

    public static void error(String message, Throwable e)
    {
        RadiusLogger ler = RadiusLog.logger;

        if (ler != null)
        {
            ler.error(message, e);
        }
    }

    public static void warn(String message)
    {
        RadiusLogger ler = RadiusLog.logger;

        if (ler != null)
        {
            ler.warn(message);
        }
    }

    public static void warn(String message, Throwable e)
    {
        RadiusLogger ler = RadiusLog.logger;

        if (ler != null)
        {
            ler.warn(message, e);
        }
    }

    public static void info(String message)
    {
        RadiusLogger ler = RadiusLog.logger;

        if (ler != null)
        {
            ler.info(message);
        }
    }

    public static void info(String message, Throwable e)
    {
        RadiusLogger ler = RadiusLog.logger;

        if (ler != null)
        {
            ler.info(message, e);
        }
    }

    public static void debug(String message)
    {
        RadiusLogger ler = RadiusLog.logger;

        if (ler != null)
        {
            ler.debug(message);
        }
    }

    public static void debug(String message, Throwable e)
    {
        RadiusLogger ler = RadiusLog.logger;

        if (ler != null)
        {
            ler.debug(message, e);
        }
    }

    /*public static String problem(JRadiusRequest request, JRadiusSession session, RadiusException a2.exception, String mesg)
    {
        StringBuffer sb = new StringBuffer();
        if (mesg != null)
        {
            sb.append("Problem: ").append(mesg).append("\n\n");
        }

        if (request != null)
        {
            sb.append("-----------------------------------------------------------\n")
              .append("JRadiusRequest: ").append(request.toString()).append("\n")
              .append("-----------------------------------------------------------\n");

            try
            {
                sb.append("RADIUS Request:\n")
                  .append("-----------------------------------------------------------\n")
                  .append(request.getRequestPacket().toString())
                  .append("-----------------------------------------------------------\n")
                  .append("RADIUS Reply:\n")
                  .append("-----------------------------------------------------------\n")
                  .append(request.getReplyPacket().toString())
                  .append("-----------------------------------------------------------\n")
                  .append("\n\n");
            }
            catch (RadiusException e)
            {
            }
        }

        if (session != null)
        {
            sb.append("-----------------------------------------------------------\n")
              .append("RadiusSession:\n")
              .append("-----------------------------------------------------------\n")
              .append(session.toString())
              .append("\n\n");
        }

        if (a2.exception != null)
        {
            sb.append("-----------------------------------------------------------\n")
              .append("RadiusException:\n")
              .append("-----------------------------------------------------------\n")
              .append(a2.exception.toString())
              .append("\n\n");
        }
        return sb.toString();
    }*/

    public static RadiusLogger getRadiusLogger()
    {
        return RadiusLog.logger;
    }

    public static void setRadiusLogger(RadiusLogger logger)
    {
        if (logger == null)
        {
            throw new IllegalArgumentException("logger");
        }

        RadiusLog.logger = logger;
    }

    private static RadiusLogger logger = new Log4JRadiusLogger();
}
