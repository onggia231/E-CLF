package a2.server;

import org.apache.commons.pool.ObjectPool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * @author David Bird
 */
public abstract class ListenerRequest {
    protected JRadiusEvent event;
    protected Listener listener;
    protected ObjectPool borrowedFromPool;

    public ListenerRequest() {
    }

    public ListenerRequest(Listener listener) {
        this.listener = listener;
    }

    public ByteBuffer getByteBufferIn() throws IOException {
        return null;
    }

    public ByteBuffer getByteBufferOut() throws IOException {
        return null;
    }

    public abstract InputStream getInputStream() throws IOException;

    public abstract OutputStream getOutputStream() throws IOException;

    public abstract Map<String, String> getServerVariables();

    public Listener getListener() {
        return listener;
    }

    public void getListener(Listener listener) {
        this.listener = listener;
    }

    public JRadiusEvent getEventFromListener() throws Exception {
        JRadiusEvent e = listener.parseRequest(this, getByteBufferIn(), getInputStream());
        if (e == null) return null;
        e.setListener(listener);
        return e;
    }

    public JRadiusEvent getRequestEvent() throws Exception {
        if (event == null) {
            event = getEventFromListener();
        }

        return event;
    }

    public void clear() {
        event = null;
    }

    public ObjectPool getBorrowedFromPool() {
        return borrowedFromPool;
    }

    public void setBorrowedFromPool(ObjectPool borrowedFromPool) {
        this.borrowedFromPool = borrowedFromPool;
    }
}
