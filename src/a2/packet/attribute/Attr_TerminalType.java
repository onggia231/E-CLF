package a2.packet.attribute;

import a2.packet.attribute.value.IntegerValue;

import java.io.Serializable;

public class Attr_TerminalType extends A2Attribute {
    public static final String NAME = "Terminal-Type";
    public static final long TYPE = 0x08;
    private static final long serialVersionUID = TYPE;

    @Override
    public void setup() {
        attributeName = NAME;
        attributeType = TYPE;
        attributeValue = new IntegerValue();
        ((IntegerValue) attributeValue).setLength(2);
    }

    public Attr_TerminalType() {
        setup();
    }

    public Attr_TerminalType(Serializable o) {
        setup(o);
    }
}
