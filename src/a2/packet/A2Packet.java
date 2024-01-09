package a2.packet;


import a2.exception.UnknownAttributeException;
import a2.packet.attribute.A2Attribute;
import a2.packet.attribute.AttributeList;
import a2.packet.attribute.value.AttributeValue;

import java.io.Serializable;

/**
 * Represents a Radius Packet. All radius packet classes are derived
 * from this abstract class.
 *
 * @author David Bird
 */
public abstract class A2Packet implements Serializable {
    private static final long serialVersionUID = 0L;
    public static final int MIN_PACKET_LENGTH = 20;
    public static final int MAX_PACKET_LENGTH = 4096;
    public static final short RADIUS_HEADER_LENGTH = 4;

    protected int code;
    protected int version = 0x02;
    protected final AttributeList attributes = new AttributeList();

    protected boolean recyclable;
    protected boolean recycled;

    /**
     * Default Constructor
     */
    public A2Packet() {
    }

    /**
     * Constructs a RadiusPacket with an AttributeList
     *
     * @param list Initial AttributeList
     */
    public A2Packet(AttributeList list) {
        if (list != null) {
            attributes.copy(list, recyclable);
        }
    }

    /**
     * @param code The code to set
     */
    public void setCode(int code) {
        this.code = (byte) code;
    }

    /**
     * @return Returns the code of the RadiusPacket
     */
    public int getCode() {
        return code;
    }

    /**
     * Adds an a2.packet.attribute to a RadiusPacket (without overriding any
     * existing attributes)
     *
     * @param attribute The a2.packet.attribute to add
     */
    public void addAttribute(A2Attribute attribute) {
        if (null != attribute) {
            attributes.add(attribute, false);
        }
    }

    /**
     * Adds an a2.packet.attribute to a RadiusPacket overwriting any existing a2.packet.attribute
     *
     * @param attribute The a2.packet.attribute to add
     */
    public void overwriteAttribute(A2Attribute attribute) {
        if (null != attribute) {
            attributes.add(attribute, true);
        }
    }

    /**
     * Adds the contents of an AttributeList to a RadiusPacket
     *
     * @param list The attributes to add
     */
    public void addAttributes(AttributeList list) {
        attributes.add(list);
    }

    /**
     * Removes an a2.packet.attribute
     *
     * @param attribute The RadiusAttribute to be removed
     */
    public void removeAttribute(A2Attribute attribute) {
        attributes.remove(attribute);
    }

    /**
     * Removes an a2.packet.attribute
     *
     * @param attributeType The a2.packet.attribute type to be removed
     */
    public void removeAttribute(long attributeType) {
        attributes.remove(attributeType);
    }

    /**
     * Get the Identifier of the RadiusPacket (creating one if needed)
     *
     * @return Returns the RadiusPacket Identifier
     */
    public int getVersion() {
        if (this.version < 0) {
            this.version = 0x02;
        }
        return this.version;
    }

    /**
     * Set the Identifier byte of a RadiusPacket
     *
     * @param i The new Identifier
     */
    public void setVersion(int i) {
        this.version = i;
    }

    /**
     * Get the attributes of a RadiusPacket
     *
     * @return Returns the AttributeList of the packet
     */
    public AttributeList getAttributes() {
        return attributes;
    }

    /**
     * Derived RadiusRequest classes must override this
     *
     * @param attributes
     * @return Returns 16 bytes
     */
    public byte[] createAuthenticator(byte[] attributes, int offset, int attributsLength, String sharedSecret) {
        return new byte[16];
    }

    /**
     * @param type The a2.packet.attribute type
     * @return Returns the a2.packet.attribute, if found
     */
    public A2Attribute findAttribute(long type) {
        return attributes.get(type);
    }

    /**
     * @param type The integer type of the a2.packet.attribute to find
     * @return Returns an array of RadiusAttributes
     */
    public Object[] findAttributes(long type) {
        return attributes.getArray(type);
    }

    /**
     * @param aName The name of the a2.packet.attribute to find
     * @return Returns the RadiusAttribute, null if not found
     * @throws -UnknownAttributeException
     */
    public A2Attribute findAttribute(String aName) throws UnknownAttributeException {
        return attributes.get(aName);
    }

    /**
     * @param type The integer type of the a2.packet.attribute to find
     * @return Returns the Object value of the found a2.packet.attribute, otherwise null
     */
    public Object getAttributeValue(long type) {
        return attributes.getValue(type);
    }

    /**
     * @param aName The name of the a2.packet.attribute to find
     * @return Returns the Object value of the found a2.packet.attribute, otherwise null
     * @throws UnknownAttributeException
     */
    public Object getAttributeValue(String aName)
            throws UnknownAttributeException {
        A2Attribute attribute = findAttribute(aName);
        if (attribute != null) {
            AttributeValue value = attribute.getValue();
            if (value != null) {
                return value.getValueObject();
            }
        }
        return null;
    }

    /**
     * Formats the RadiusPacket into a String
     */
    public String toString(boolean nonStandardAtts, boolean unknownAttrs) {
        StringBuffer sb = new StringBuffer();
        sb.append("Class: ").append(this.getClass().toString()).append("\n");
        sb.append("Attributes:\n");
        sb.append(attributes.toString(nonStandardAtts, unknownAttrs));
        return sb.toString();
    }

    public String toString() {
        return toString(true, true);
    }

    public boolean isRecyclable() {
        return recyclable;
    }
}
