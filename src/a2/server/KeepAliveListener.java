package a2.server;

import a2.log.RadiusLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * @author David Bird
 */
public class KeepAliveListener extends JRadiusThread {
    private Socket socket;
    private TCPListener listener;
    private BlockingQueue<ListenerRequest> queue;
    private BufferedInputStream bin;
    private BufferedOutputStream bout;

    public KeepAliveListener(Socket socket, TCPListener listener, BlockingQueue<ListenerRequest> queue) throws IOException {
        this.socket = socket;
        this.bin = new BufferedInputStream(socket.getInputStream(), 4096);
        this.bout = new BufferedOutputStream(socket.getOutputStream(), 4096);
        this.listener = listener;
        this.queue = queue;
    }

    public void run() {
        RadiusLog.debug(this.getClass().getName() + ".run(): starting tcp socket listener");

        try {
            while (true) {
                TCPListenerRequest lr = (TCPListenerRequest) listener.requestObjectPool.borrowObject();
                lr.setBorrowedFromPool(listener.requestObjectPool);
                lr.accept(this.socket, this.bin, this.bout, this.listener, true, true);

                if (lr == null || lr.event == null) {
                    RadiusLog.info(this.getClass().getName() + ".run(): shutting down tcp socket listener");
                    break;
                }

                // enqueue item to list so one of processors can start processing
                while (true) {
                    try {
                        this.queue.put(lr);
                        break;
                    } catch (InterruptedException e) {
                    }
                }
            }
        } catch (Exception e) {
            RadiusLog.info(this.getClass().getName() + ".run(): shutting down tcp socket listener", e);
        }

        shutdown(false);

        listener.deadKeepAliveListener(this);
    }

    public void shutdown(boolean tryToInterrupt) {
        if (this.socket != null) {
            try {
                this.socket.shutdownInput();
            } catch (Exception e) {
            }

            try {
                this.socket.shutdownOutput();
            } catch (Exception e) {
            }

            try {
                this.socket.close();
            } catch (Exception e) {
            }

            this.socket = null;

            if (tryToInterrupt) {
                try {
                    this.interrupt();
                } catch (Exception e) {
                }
            }
        }
    }
}
