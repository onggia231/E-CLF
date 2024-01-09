package a2.test;

import a2.log.Log4JRadiusLogger;
import a2.packet.*;
import a2.packet.attribute.AttributeFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class A2Client {
    private static final Log log = LogFactory.getLog(A2Client.class);
    protected final ByteBuffer buffer_in;
    protected final ByteBuffer buffer_out;

    static {
        AttributeFactory.loadA2Dictionary();
    }

    public A2Client() {
        buffer_in = ByteBuffer.allocate(25000);
        buffer_in.order(ByteOrder.BIG_ENDIAN);

        buffer_out = ByteBuffer.allocate(25000);
        buffer_out.order(ByteOrder.BIG_ENDIAN);
    }

    public static void main(String[] args) throws Exception {
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", 6543);
            socket.setSoTimeout(30 * 1000);
            DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
            outToServer.write(new byte[0]);
            System.out.println("Send package to server.....");

            A2Request request = new NewIPAccessContextRequest(99, 01, "Paris#3#2#114#16",
                    "a1s3fs3f379f42er3g34!312sg25t4gf323", "2001:db8:85a3:8d3:1319:8a2e:370:7348", "10.10.10.1", "999");
            A2Client client = new A2Client();
            client.send(socket, request);
            client.receive(socket);
        } catch (Exception e) {
            throw e;
        } finally {
            socket.close();
        }
    }

    protected void send(Socket socket, A2Request req) throws Exception {
        A2Format format = A2Format.getInstance();
        OutputStream out = socket.getOutputStream();

        buffer_out.clear();
        format.packPacket(req, buffer_out, true);

        synchronized (out) {
            /*if (statusListener != null)
                statusListener.onBeforeSend(this, req);*/

            out.write(buffer_out.array(), 0, buffer_out.position());

            /*if (statusListener != null)
                statusListener.onAfterSend(this);*/
        }
    }

    protected A2Response receive(Socket socket) throws Exception {
        A2Response res = null;
        DataInputStream in = new DataInputStream(socket.getInputStream());

        synchronized (in) {

            int version = A2Format.readUnsignedByte(in);
            int opCode = A2Format.readUnsignedByte(in);
            int length = A2Format.readUnsignedShort(in);
            System.out.println("version=" + version + ", opCode=" + opCode + ", length=" + length);
            buffer_in.clear();
            buffer_in.limit(in.read(buffer_in.array(), 0, length));
            A2Packet response = new NewIPAccessContextResponse();
            A2Format.setAttributeBytes(response, buffer_in, length);
            System.out.printf("Response: ", response);
        }

        return res;
    }
}
