package a2.server;

import a2.log.RadiusLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.SoftReferenceObjectPool;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * The base abstract class of all Listeners
 *
 * @author Gert Jan Verhoog
 * @author David Bird
 */
public abstract class TCPListener extends JRadiusThread implements Listener {
    protected Log log = LogFactory.getLog(getClass());

    protected boolean active = false;

    protected BlockingQueue<ListenerRequest> queue;

    protected int port = 1814;
    protected int backlog = 1024;
    protected boolean requiresSSL = false;
    protected boolean usingSSL = false;
    protected boolean keepAlive;

    protected ServerSocket serverSocket;

    protected final List<KeepAliveListener> keepAliveListeners = new LinkedList<KeepAliveListener>();

    protected ObjectPool requestObjectPool = new SoftReferenceObjectPool(new PoolableObjectFactory() {
        public boolean validateObject(Object arg0) {
            return true;
        }

        public void passivateObject(Object arg0) throws Exception {
        }

        public Object makeObject() throws Exception {
            return new TCPListenerRequest();
        }

        public void destroyObject(Object arg0) throws Exception {
        }

        public void activateObject(Object arg0) throws Exception {
            TCPListenerRequest req = (TCPListenerRequest) arg0;
            req.clear();
        }
    });


    public void setConfiguration() {
        try {
            setConfiguration(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setConfiguration(boolean noKeepAlive) throws IOException {
        keepAlive = !noKeepAlive;
        serverSocket = new ServerSocket(port, backlog);
        serverSocket.setReuseAddress(true);
        setActive(true);
    }

    /**
     * Sets the request queue for this listener
     *
     * @param q the RequestQueue;
     */
    public void setRequestQueue(BlockingQueue<ListenerRequest> q) {
        queue = q;
    }

    /**
     * Listen for one object and place it on the request queue
     *
     * @throws IOException
     * @throws InterruptedException
     * @throws -RadiusException
     */
    public void listen() throws Exception {
        RadiusLog.debug("Listening on socket...");
        Socket socket = serverSocket.accept();

        socket.setTcpNoDelay(false);

        if (keepAlive) {
            KeepAliveListener keepAliveListener = new KeepAliveListener(socket, this, queue);
            keepAliveListener.start();

            synchronized (keepAliveListeners) {
                keepAliveListeners.add(keepAliveListener);
            }
        } else {
            TCPListenerRequest lr = (TCPListenerRequest) requestObjectPool.borrowObject();
            lr.setBorrowedFromPool(requestObjectPool);
            lr.accept(socket, this, false, false);

            while (true) {
                try {
                    this.queue.put(lr);
                    break;
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public void deadKeepAliveListener(KeepAliveListener keepAliveListener) {
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (!active) {
            for (KeepAliveListener listener : keepAliveListeners) {
                try {
                    listener.shutdown(true);
                } catch (Throwable e) {
                }
            }

            this.keepAliveListeners.clear();

            try {
                this.serverSocket.close();
            } catch (Throwable e) {
            }

            try {
                this.interrupt();
            } catch (Exception e) {
            }
        }
    }

    /**
     * The thread's run method repeatedly calls listen()
     */
    public void run() {
        while (getActive()) {
            try {
                Thread.yield();
                listen();
            } catch (SocketException e) {
                if (getActive() == false) {
                    break;
                } else {
                    RadiusLog.error("Socket exception", e);
                }
            } catch (InterruptedException e) {
            } catch (SSLException e) {
                RadiusLog.error("Error occured in TCPListener.", e);
                active = false;
            } catch (Throwable e) {
                RadiusLog.error("Error occured in TCPListener.", e);
            }
        }

        RadiusLog.debug("Listener: " + this.getClass().getName() + " exiting (not active)");
    }

    public boolean isUsingSSL() {
        return usingSSL;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUsingSSL(boolean usingSSL) {
        this.usingSSL = usingSSL;
    }
}
