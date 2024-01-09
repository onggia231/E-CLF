package a2.packet;

import a2.log.RadiusLog;
import a2.packet.attribute.A2Attribute;
import a2.packet.attribute.Format;
import a2.packet.attribute.VSAttribute;
import a2.packet.attribute.value.AttributeValue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;


/**
 * Default RadiusPacket/RadiusAttribute format class. This class formats
 * and parses UDP RADIUS Packets. Derived classes implement other formats.
 *
 * @author David Bird
 */
public class A2Format extends Format {
    private static final int HEADER_LENGTH = 3;
    public static final int VSA_HEADER_LENGTH = 8;

    private static final A2Format staticFormat = new A2Format();

    /**
     * @return Returns a static instnace of this class
     */
    public static A2Format getInstance() {
        return staticFormat;
    }

    /**
     * Parses attributes and places them in a RadiusPacket
     *
     * @param packet       The RadiusPacket to parse attributes into
     * @param -bAttributes The a2.packet.attribute bytes to parse
     */
    public static void setAttributeBytes(A2Packet packet, ByteBuffer buffer, int length) {
        staticFormat.unpackAttributes(packet.getAttributes(), buffer, length, packet.isRecyclable());
    }

    /**
     * Packs a A2Packet into a byte array
     *
     * @param packet The A2Packet to pack
     * @return Returns the packed A2Packet
     * public byte[] packPacket(A2Packet packet, String sharedSecret) throws IOException
     * {
     * return packPacket(packet, sharedSecret, false);
     * }
     */

    public void packPacket(A2Packet packet, String sharedSecret, ByteBuffer buffer, boolean onWire) throws IOException {
        if (packet == null) {
            throw new IllegalArgumentException("Packet is null.");
        }

        int initialPosition = buffer.position();
        buffer.position(initialPosition + A2Packet.RADIUS_HEADER_LENGTH);
        packAttributeList(packet.getAttributes(), buffer, onWire);

        int totalLength = buffer.position() - initialPosition;
        int attributesLength = totalLength - A2Packet.RADIUS_HEADER_LENGTH;

        try {
            buffer.position(initialPosition);
            packHeader(buffer, packet, buffer.array(), initialPosition + A2Packet.RADIUS_HEADER_LENGTH, attributesLength, sharedSecret);
            buffer.position(totalLength + initialPosition);
        } catch (Exception e) {
            RadiusLog.warn(e.getMessage(), e);
        }
    }

    /*
     *   HườngNV
     */
    public void packPacket(A2Packet packet, ByteBuffer buffer, boolean onWire) {
        if (packet == null) {
            throw new IllegalArgumentException("Packet is null.");
        }

        int initialPosition = buffer.position();
        buffer.position(initialPosition + A2Packet.RADIUS_HEADER_LENGTH);
        packAttributeList(packet.getAttributes(), buffer, onWire);

        int totalLength = buffer.position() - initialPosition;
        int attributesLength = totalLength - A2Packet.RADIUS_HEADER_LENGTH;

        buffer.position(initialPosition);
        packHeader(buffer, packet, attributesLength);
        buffer.position(totalLength + initialPosition);
    }

    /*
    public byte[] packPacket(A2Packet packet, String sharedSecret, boolean onWire) throws IOException
    {
        if (packet == null)
        {
            throw new IllegalArgumentException("Packet is null.");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] attributeBytes = packAttributeList(packet.getAttributes(), onWire);
        
        try
        {
            packHeader(out, packet, attributeBytes, sharedSecret);
            if (attributeBytes != null) out.write(attributeBytes);
            out.close();
        }
        catch(Exception e)
        {
            RadiusLog.warn(e.getMessage(), e);
        }
        
        return out.toByteArray();
    }
    */

    /**
     * Packs the A2Packet into a DataOutputStream
     *
     * @param -out           The DataOutputStream to write to
     * @param p              The A2Packet to pack
     * @param attributeBytes The A2Packet attributes
     * @throws IOException public void packHeader(OutputStream out, A2Packet p, byte[] attributeBytes, String sharedSecret) throws IOException
     *                     {
     *                     int length = attributeBytes.length + A2Packet.RADIUS_HEADER_LENGTH;
     *                     writeUnsignedByte(out, p.getCode());
     *                     writeUnsignedByte(out, p.getIdentifier());
     *                     writeUnsignedShort(out, length);
     *                     out.write(p.getAuthenticator(attributeBytes, sharedSecret));
     *                     }
     */

    public void packHeader(ByteBuffer buffer, A2Packet p, byte[] attributeBytes, int offset, int attributesLength, String sharedSecret) throws IOException {
        int length = attributesLength + A2Packet.RADIUS_HEADER_LENGTH;
        putUnsignedByte(buffer, p.getCode());
        putUnsignedByte(buffer, p.getVersion());
        putUnsignedShort(buffer, length);
        /*buffer.put(p.getAuthenticator(attributeBytes, offset, attributesLength, sharedSecret));*/
    }

    /*
     * HườngNV
     */
    public void packHeader(ByteBuffer buffer, A2Packet p, int attributesLength) {
        int length = attributesLength + A2Packet.RADIUS_HEADER_LENGTH;
        putUnsignedByte(buffer, p.getVersion());
        putUnsignedByte(buffer, p.getCode());
        putUnsignedShort(buffer, length);
    }

    /**
     * Packs a RadiusAttribute into a DataOutputStream
     *
     * @param -out The DataOutputStream to write attributes to
     * @param a    The RadiusAttribute to pack
     * @throws IOException public void packAttribute(OutputStream out, RadiusAttribute a) throws IOException
     *                     {
     *                     AttributeValue attributeValue = a.getValue();
     *                     packHeader(out, a);
     *                     attributeValue.getBytes(out);
     *                     }
     */

    public void packAttribute(ByteBuffer buffer, A2Attribute a) {
        AttributeValue attributeValue = a.getValue();

        if (a instanceof VSAttribute) {
            VSAttribute vsa = (VSAttribute) a;
            if (vsa.hasContinuationByte()) {
                int headerLength = headerLength(vsa);
                int valueLength = attributeValue.getLength();
                int maxLength = 255 - headerLength;
                int len;
                if (valueLength > maxLength) {
                    for (int off = 0; off < valueLength; off += maxLength) {
                        len = valueLength - off;
                        if (len > maxLength) {
                            len = maxLength;
                            vsa.setContinuation();
                        } else {
                            vsa.unsetContinuation();
                        }
                        packHeader(buffer, a, len);
                        attributeValue.getBytes(buffer, off, len);
                    }
                    return;
                }
            }
        }

        packHeader(buffer, a);
        attributeValue.getBytes(buffer);
    }

    /**
     * Packs a RadiusAttribute header into a DataOutputStream
     *
     * @param -out The DataOutputStream to write to
     * @param a    The RadiusAttribute to pack
     * @throws IOException public void packHeader(OutputStream out, RadiusAttribute a) throws IOException
     *                     {
     *                     if (a instanceof VSAttribute)
     *                     {
     *                     packHeader(out, (VSAttribute)a);
     *                     return;
     *                     }
     *                     AttributeValue attributeValue = a.getValue();
     *                     writeUnsignedByte(out, (int) a.getType());
     *                     writeUnsignedByte(out, attributeValue.getLength() + HEADER_LENGTH);
     *                     }
     */

    public void packHeader(ByteBuffer buffer, A2Attribute a) {
        packHeader(buffer, a, a.getValue().getLength());
    }

    public void packHeader(ByteBuffer buffer, A2Attribute a, int valueLength) {
        if (a instanceof VSAttribute) {
            packHeader(buffer, (VSAttribute) a, valueLength);
            return;
        }
        putUnsignedByte(buffer, (int) a.getType());
        putUnsignedShort(buffer, valueLength + HEADER_LENGTH);
    }


    /**
     * Packs a VSAttribute header into a DataOutputStream
     *
     * @param -out The DataOutputStream to write to
     * @param a    The VSAttribute to pack
     * @throws IOException public void packHeader(OutputStream out, VSAttribute a) throws IOException
     *                     {
     *                     AttributeValue attributeValue = a.getValue();
     *                     int len = attributeValue.getLength();
     *                     int vsaHeader = VSA_HEADER_LENGTH;
     *                     if (a.hasContinuationByte()) vsaHeader ++;
     *                     writeUnsignedByte(out, (int)a.getType());
     *                     writeUnsignedByte(out, len + vsaHeader);
     *                     writeUnsignedInt(out, a.getVendorId());
     *                     writeUnsignedByte(out, (int)a.getVsaAttributeType());
     *                     len += 2;
     *                     if (a.hasContinuationByte()) len ++;
     *                     switch(a.getLengthLength())
     *                     {
     *                     case 1:
     *                     writeUnsignedByte(out, len);
     *                     break;
     *                     case 2:
     *                     writeUnsignedShort(out, len);
     *                     break;
     *                     case 4:
     *                     writeUnsignedInt(out, len);
     *                     break;
     *                     }
     *                     if (a.hasContinuationByte())
     *                     {
     *                     writeUnsignedByte(out, a.getContinuation());
     *                     }
     *                     }
     */

    public int headerLength(VSAttribute a) {
        int vsaHeader = 6;
        vsaHeader += a.getTypeLength();
        vsaHeader += a.getLengthLength();
        if (a.hasContinuationByte()) {
            vsaHeader++;
        }
        return vsaHeader;
    }

    public int headerLength(A2Attribute a) {
        if (a instanceof VSAttribute) {
            return headerLength((VSAttribute) a);
        }
        return HEADER_LENGTH;
    }

    public void packHeader(ByteBuffer buffer, VSAttribute a) {
        packHeader(buffer, a, a.getValue().getLength());
    }

    public void packHeader(ByteBuffer buffer, VSAttribute a, int len) {
        int vsaHeader = headerLength(a);

        /*
         *  Enforce the maximum packet length here.
         */
        if (len > (255 - vsaHeader)) {
            throw new RuntimeException("RADIUS a2.packet.attribute value too long (" + len + ")");
        }

        putUnsignedByte(buffer, (int) a.getType());
        putUnsignedByte(buffer, len + vsaHeader);
        putUnsignedInt(buffer, a.getVendorId());
        putUnsignedByte(buffer, (int) a.getVsaAttributeType());

        len += 2;
        if (a.hasContinuationByte()) {
            len++;
        }

        switch (a.getLengthLength()) {
            case 1:
                putUnsignedByte(buffer, len);
                break;
            case 2:
                putUnsignedShort(buffer, len);
                break;
            case 4:
                putUnsignedInt(buffer, len);
                break;
        }
        if (a.hasContinuationByte()) {
            putUnsignedByte(buffer, a.getContinuation());
        }
    }

    /**
     * Unpacks the header of a RadiusAttribute from a DataInputStream
     *
     * @param in  The DataInputStream to read from
     * @param ctx The Attribute Parser Context
     * @return Returns the additional offset length for this header
     * @throws IOException
     */
    public void unpackAttributeHeader(InputStream in, AttributeParseContext ctx) throws IOException {
        ctx.attributeOp = 0;
        ctx.vendorNumber = -1;
        ctx.padding = 0;

        ctx.attributeType = readUnsignedByte(in);
        ctx.attributeLength = readUnsignedByte(in);
        ctx.headerLength = 2;
    }

    public void unpackAttributeHeader(ByteBuffer buffer, AttributeParseContext ctx) throws IOException {
        ctx.attributeOp = 0;
        ctx.vendorNumber = -1;
        ctx.padding = 0;

        ctx.attributeType = getUnsignedByte(buffer);
        ctx.attributeLength = getUnsignedShort(buffer);
        ctx.headerLength = HEADER_LENGTH;
    }
}
