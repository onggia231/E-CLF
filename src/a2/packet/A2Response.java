package a2.packet;

import a2.packet.attribute.AttributeList;

public abstract class A2Response extends A2Packet {
    private static final long serialVersionUID = 1L;

    public A2Response() {
        super();
    }

    public A2Response(int version, int code, AttributeList list) {
        super(list);
        setVersion(version);
        setCode(code);
    }
}
