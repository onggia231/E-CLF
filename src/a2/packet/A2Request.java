package a2.packet;


import a2.packet.attribute.AttributeList;

/**
 * A Radius Request (either Access Request or Accounting Request)
 *
 * @author David Bird
 */
public abstract class A2Request extends A2Packet {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor
     */
    public A2Request() {
    }

    /**
     * Constructor
     *
     * @param attributes The attributes to be used
     */
    public A2Request(int version, int code, AttributeList attributes) {
        super(attributes);
        setCode(code);
        setVersion(version);
    }
}
