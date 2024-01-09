package a2.packet.attribute;

import a2.packet.attribute.value.IntegerValue;

import java.io.Serializable;

public class Attr_TransactionId extends A2Attribute {
    public static final String NAME = "Transaction-Id";
    public static final long TYPE = 0x02;
    private static final long serialVersionUID = TYPE;

    public Attr_TransactionId() {
        setup();
    }

    public Attr_TransactionId(Serializable o) {
        setup(o);
    }

    @Override
    public void setup() {
        attributeName = NAME;
        attributeType = TYPE;
        attributeValue = new IntegerValue();
    }
}
