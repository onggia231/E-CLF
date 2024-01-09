package a2.packet.attribute;

import a2.packet.attribute.value.StringValue;

import java.io.Serializable;

public class Attr_IPAddressingZone extends A2Attribute {
    public static final String NAME = "IP-Addressing-Zone";
    public static final long TYPE = 0x07;
    private static final long serialVersionUID = TYPE;

    @Override
    public void setup() {
        attributeName = NAME;
        attributeType = TYPE;
        attributeValue = new StringValue();
    }

    public Attr_IPAddressingZone() {
        setup();
    }

    public Attr_IPAddressingZone(Serializable o) {
        setup(o);
    }
}
