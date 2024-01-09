package a2.packet;

import a2.packet.attribute.*;

public class ReleaseIPAccessContextRequest extends A2Request {
    public static final byte OP_CODE = 0x0A;
    private static final long serialVersionUID = OP_CODE;
    public static final byte VERSION = 0x02;

    public ReleaseIPAccessContextRequest() {
        setVersion(VERSION);
        setCode(OP_CODE);
    }

    public ReleaseIPAccessContextRequest(String transactionId, String networkType, String callingLineIdentification,
                                         String formattedRemoteId, String terminalType, String errorCode) {
        super(VERSION, OP_CODE, new AttributeList());
        addAttribute(new Attr_TransactionId(transactionId));
        addAttribute(new Attr_NetworkType(networkType));
        addAttribute(new Attr_CallingLineIdentification(callingLineIdentification));
        addAttribute(new Attr_FormattedRemoteId(formattedRemoteId));
        addAttribute(new Attr_TerminalType(terminalType));
        addAttribute(new Attr_ErrorCode(errorCode));
    }
}
