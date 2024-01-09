package a2.packet.attribute;

import a2.packet.attribute.value.IntegerValue;

import java.io.Serializable;

public class Attr_NetworkType extends A2Attribute {
    public static final String NAME = "Network-Type";
    public static final long TYPE = 0x0A;
    private static final long serialVersionUID = TYPE;

    @Override
    public void setup() {
        attributeName = NAME;
        attributeType = TYPE;
        attributeValue = new IntegerValue();
        ((IntegerValue) attributeValue).setLength(1);
    }

    public Attr_NetworkType() {
        setup();
    }

    public Attr_NetworkType(Serializable o) {
        setup(o);
    }
}
