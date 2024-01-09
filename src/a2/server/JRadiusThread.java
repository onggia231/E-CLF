package a2.server;

/**
 * Thread manager for JRadius.
 * @author David Bird
 */
public class JRadiusThread extends Thread
{
    private static int threadCount = 0;
    private synchronized int getThreadNumber() { return ++threadCount; }

    public JRadiusThread()
    {
        setName(this.getClass().getName() + "(" + getThreadNumber() + ")");
    }
    
    public JRadiusThread(Runnable runnable)
    {
        super(runnable);
        setName(this.getClass().getName() + "(" + getThreadNumber() + ")");
    }
}
