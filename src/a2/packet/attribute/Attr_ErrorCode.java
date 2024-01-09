package a2.packet.attribute;

import a2.packet.attribute.value.IntegerValue;

import java.io.Serializable;

public class Attr_ErrorCode extends A2Attribute {
    public static final String NAME = "Error-Code";
    public static final long TYPE = 0x09;
    private static final long serialVersionUID = TYPE;

    @Override
    public void setup() {
        attributeName = NAME;
        attributeType = TYPE;
        attributeValue = new IntegerValue();
        ((IntegerValue) attributeValue).setLength(1);
    }

    public Attr_ErrorCode() {
        setup();
    }

    public Attr_ErrorCode(Serializable o) {
        setup(o);
    }
}
