package a2.packet;

import a2.packet.attribute.Attr_ErrorCode;
import a2.packet.attribute.Attr_TransactionId;
import a2.packet.attribute.AttributeList;

public class ReleaseIPAccessContextResponse extends A2Response {
    public static final byte OP_CODE = 0x0B;
    private static final long serialVersionUID = OP_CODE;
    public static final byte VERSION = 0x02;

    public ReleaseIPAccessContextResponse(String transactionId, String errorCode) {
        super(VERSION, OP_CODE, new AttributeList());
        addAttribute(new Attr_TransactionId(transactionId));
        addAttribute(new Attr_ErrorCode(errorCode));
    }
}
