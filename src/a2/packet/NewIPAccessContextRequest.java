package a2.packet;

import a2.packet.attribute.*;

public class NewIPAccessContextRequest extends A2Request {
    public static final byte OP_CODE = 0x08;
    private static final long serialVersionUID = OP_CODE;
    public static final byte VERSION = 0x02;

    public NewIPAccessContextRequest() {
        setVersion(VERSION);
        setCode(OP_CODE);
    }

    public NewIPAccessContextRequest(int transactionId, int networkType, String callingLineIdentification, String formattedRemoteId,
                                     String ipAddress, String ipAddressingZone, String terminalType) {
        super(VERSION, OP_CODE, new AttributeList());
        addAttribute(new Attr_TransactionId(transactionId));
        addAttribute(new Attr_NetworkType(networkType));
        addAttribute(new Attr_CallingLineIdentification(callingLineIdentification));
        addAttribute(new Attr_FormattedRemoteId(formattedRemoteId));
        addAttribute(new Attr_IPAddress(ipAddress));
        addAttribute(new Attr_IPAddressingZone(ipAddressingZone));
        addAttribute(new Attr_TerminalType(terminalType));
    }
}
