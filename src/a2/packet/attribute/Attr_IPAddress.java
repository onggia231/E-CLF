package a2.packet.attribute;

import a2.packet.attribute.value.IPAddrValue;

import java.io.Serializable;

public class Attr_IPAddress extends A2Attribute {
    public static final String NAME = "IP-Address";
    public static final long TYPE = 0x01;
    private static final long serialVersionUID = TYPE;

    public Attr_IPAddress() {
        setup();
    }

    public Attr_IPAddress(Serializable o) {
        setup(o);
    }

    @Override
    public void setup() {
        attributeName = NAME;
        attributeType = TYPE;
        attributeValue = new IPAddrValue();
    }
}
