package a2.server;


import org.apache.commons.pool.ObjectPool;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;

public interface Listener {

//	public void setConfiguration(ListenerConfigurationItem cfg) throws Exception;

    public void setRequestQueue(BlockingQueue<ListenerRequest> queue);

    public void setRequestObjectPool(ObjectPool pool);

    public String getName();

    public JRadiusEvent parseRequest(ListenerRequest listenerRequest, ByteBuffer byteBuffer, InputStream inputStream) throws Exception;

    public void start();

    public void stop();

    public boolean getActive();

    public void setActive(boolean active);
}
