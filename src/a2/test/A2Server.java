package a2.test;

import a2.exception.UnknownAttributeException;
import a2.packet.A2Format;
import a2.packet.A2Packet;
import a2.packet.NewIPAccessContextRequest;
import a2.packet.NewIPAccessContextResponse;
import a2.packet.attribute.Attr_TransactionId;
import a2.packet.attribute.AttributeFactory;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class A2Server {
    protected final ByteBuffer buffer_in;
    protected final ByteBuffer buffer_out;

    public A2Server() {
        buffer_in = ByteBuffer.allocate(25000);
        buffer_in.order(ByteOrder.BIG_ENDIAN);

        buffer_out = ByteBuffer.allocate(25000);
        buffer_out.order(ByteOrder.BIG_ENDIAN);
//        AttributeFactory.loadAttributeDictionary((AttributeDictionary)Configuration.getBean(dictionaryConfig.getClassName()));
    }

    public void startServer() throws IOException, UnknownAttributeException {
        ByteBuffer buffer = buffer_in;
        //Tạo socket server, chờ tại cổng '6543'
        ServerSocket server = new ServerSocket(6543);

        while (true) {
            AttributeFactory.loadA2Dictionary();
            //chờ yêu cầu từ client
            Socket socket = server.accept();

            //Tạo input stream, nối tới Socket
            DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
            int version = A2Format.readUnsignedByte(in);
            int opCode = A2Format.readUnsignedByte(in);
            int length = A2Format.readUnsignedShort(in);
            A2Packet request = new NewIPAccessContextRequest();
            System.out.println("version=" + version + ", opCode=" + opCode + ", length=" + length);
            length -= A2Packet.RADIUS_HEADER_LENGTH;
            buffer.clear();
            if (length > 0) {
                buffer.limit(in.read(buffer.array(), 0, length));
                A2Format.setAttributeBytes(request, buffer, length);
                System.out.println(request);
            }
            if (0x08 == opCode) {
                A2Format format = A2Format.getInstance();
                NewIPAccessContextResponse response = new NewIPAccessContextResponse(Integer.parseInt(request.getAttributeValue(Attr_TransactionId.TYPE).toString()), 0);
                buffer_out.clear();
                format.packPacket(response, buffer_out, true);
                synchronized (outToClient) {
                    outToClient.write(buffer_out.array(), 0, buffer_out.position());
                }
            }
//            byte[] messageTLV = new byte[_length - 4];
            /*boolean end = false;
            while (!end) {

            }*/
            //Tạo outputStream, nối tới socket

            //ghi dữ liệu ra socket
//            outToClient.writeBytes(sentence_to_client);
        }
    }

    public static void main(String[] args) throws IOException, UnknownAttributeException {
        A2Server server = new A2Server();
        server.startServer();
        System.out.println("Server started...");
    }
}
