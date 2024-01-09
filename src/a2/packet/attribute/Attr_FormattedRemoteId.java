package a2.packet.attribute;

import a2.packet.attribute.value.StringValue;

import java.io.Serializable;

public class Attr_FormattedRemoteId extends A2Attribute {
    public static final String NAME = "Formatted-RemoteId";
    public static final long TYPE = 0x0B;
    private static final long serialVersionUID = TYPE;

    @Override
    public void setup() {
        attributeName = NAME;
        attributeType = TYPE;
        attributeValue = new StringValue();
    }

    public Attr_FormattedRemoteId() {
        setup();
    }

    public Attr_FormattedRemoteId(Serializable o) {
        setup(o);
    }
}
