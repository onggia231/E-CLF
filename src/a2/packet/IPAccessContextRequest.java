package a2.packet;

import a2.packet.attribute.Attr_ErrorCode;
import a2.packet.attribute.Attr_IPAddress;
import a2.packet.attribute.Attr_TransactionId;
import a2.packet.attribute.AttributeList;

public class IPAccessContextRequest extends A2Request {
    public static final byte OP_CODE = 0x0C;
    private static final long serialVersionUID = OP_CODE;
    public static final byte VERSION = 0x02;

    public IPAccessContextRequest() {
        setVersion(VERSION);
        setCode(OP_CODE);
    }

    public IPAccessContextRequest(String transactionId, String ipAddress, String errorCode) {
        super(VERSION, OP_CODE, new AttributeList());
        addAttribute(new Attr_TransactionId(transactionId));
        addAttribute(new Attr_IPAddress(ipAddress));
        addAttribute(new Attr_ErrorCode(errorCode));
    }
}
