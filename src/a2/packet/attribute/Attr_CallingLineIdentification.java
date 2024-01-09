package a2.packet.attribute;

import a2.packet.attribute.value.OctetsValue;
import a2.packet.attribute.value.StringValue;

import java.io.Serializable;

public class Attr_CallingLineIdentification extends A2Attribute {
    public static final String NAME = "Calling-Line-Identification";
    public static final long TYPE = 0x06;
    private static final long serialVersionUID = TYPE;

    @Override
    public void setup() {
        attributeName = NAME;
        attributeType = TYPE;
        attributeValue = new StringValue();
    }

    public Attr_CallingLineIdentification() {
        setup();
    }

    public Attr_CallingLineIdentification(Serializable o) {
        setup(o);
    }
}
