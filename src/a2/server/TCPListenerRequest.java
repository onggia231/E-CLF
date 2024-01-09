package a2.server;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author David Bird
 */
public class TCPListenerRequest extends ListenerRequest {
    private Socket socket;
    private InputStream bin;
    private OutputStream bout;
    boolean keepAlive;

    public TCPListenerRequest() {
    }

    public TCPListenerRequest(Socket socket, Listener listener, boolean getEvent, boolean keepAlive) throws Exception {
        accept(socket, listener, getEvent, keepAlive);
    }

    public TCPListenerRequest(Socket socket, InputStream bin, OutputStream bout, Listener listener, boolean getEvent, boolean keepAlive) throws Exception {
        accept(socket, bin, bout, listener, getEvent, keepAlive);
    }

    public void accept(Socket socket, Listener listener, boolean getEvent, boolean keepAlive) throws Exception {
        accept(socket, new BufferedInputStream(socket.getInputStream(), 4096), new BufferedOutputStream(socket.getOutputStream(), 4096), listener, getEvent, keepAlive);
    }

    public void accept(Socket socket, InputStream bin, OutputStream bout, Listener listener, boolean getEvent, boolean keepAlive) throws Exception {
        this.listener = listener;
        this.socket = socket;
        this.bin = bin;
        this.bout = bout;
        this.keepAlive = keepAlive;

        if (getEvent) {
            this.event = getEventFromListener();
        }
    }

    public InputStream getInputStream() throws IOException {
        return bin;
    }

    public OutputStream getOutputStream() throws IOException {
        return bout;
    }

    public Map<String, String> getServerVariables() {
        Map<String, String> result = new HashMap<String, String>();
        result.put("REMOTE_ADDR", socket.getInetAddress().getHostAddress());
        return result;
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isKeepAlive() {
        return this.keepAlive;
    }
}
