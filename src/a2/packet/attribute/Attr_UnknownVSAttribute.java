package a2.packet.attribute;

import a2.packet.attribute.value.OctetsValue;

/**
 * @author David Bird
 */
public class Attr_UnknownVSAttribute extends VSAttribute implements UnknownAttribute {
    private static final long serialVersionUID = 0L;
    public static final String NAME = "Unknown-VSAttribute";

    public void setup() {
    }

    public void setup(long vendorId, long vsaAttributeType) {
        attributeName = NAME + "(" + vendorId + ":" + vsaAttributeType + ")";
        attributeType = 26;
        this.vendorId = vendorId;
        this.vsaAttributeType = vsaAttributeType;
    }

    public Attr_UnknownVSAttribute(long vendorId, long vsaAttributeType) {
        setup(vendorId, vsaAttributeType);
        attributeValue = new OctetsValue();
    }

    public Attr_UnknownVSAttribute(long vendorId, long vsaAttributeType, OctetsValue v) {
        setup(vendorId, vsaAttributeType);
        attributeValue = v;
    }

    public Attr_UnknownVSAttribute(long vendorId, long vsaAttributeType, byte[] v) {
        setup(vendorId, vsaAttributeType);
        attributeValue = new OctetsValue(v);
    }

    public long getAttributeType() {
        return ((vendorId & 0xFFFF) << 16) | vsaAttributeType;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
}
