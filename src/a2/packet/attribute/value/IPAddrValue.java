package a2.packet.attribute.value;

import a2.log.RadiusLog;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;


/**
 * The IPv4 a2.packet.attribute value
 *
 * @author David Bird
 */
public class IPAddrValue extends AttributeValue {
    private static final long serialVersionUID = 0L;
    protected InetAddress inetAddressValue;

    public IPAddrValue() {
    }

    public IPAddrValue(InetAddress i) {
        inetAddressValue = i;
    }

    public IPAddrValue(String s) {
        setValue(s);
    }

    public IPAddrValue(byte[] bytes) {
        setValue(bytes);
    }

    public void copy(AttributeValue value) {
        IPAddrValue ipValue = (IPAddrValue) value;
        this.inetAddressValue = ipValue.inetAddressValue;
    }

    public void setValue(String s) {
        try {
            inetAddressValue = InetAddress.getByName(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getLength() {
        if (inetAddressValue instanceof Inet4Address)
            return 4;
        else if (inetAddressValue instanceof Inet6Address)
            return 16;
        else
            throw new RuntimeException("Wrong IP address size for a2.packet.attribute");
    }

    public void getBytes(OutputStream out) throws IOException {
        if (inetAddressValue != null) {
            byte[] a = inetAddressValue.getAddress();
            if (!(a.length == 4 || a.length == 16))
                throw new RuntimeException("Wrong IP address size for a2.packet.attribute");
            out.write(a);
        }
    }

    public void getBytes(ByteBuffer buffer) {
        if (inetAddressValue != null) {
            byte[] a = inetAddressValue.getAddress();
            if (!(a.length == 4 || a.length == 16))
                throw new RuntimeException("Wrong IP address size for a2.packet.attribute");
            buffer.put(a);
        }
    }

    public void setValue(byte[] b) {
        if (b == null) return;
        try {
            if (!(b.length == 4 || b.length == 16))
                throw new RuntimeException("Wrong IP address size for a2.packet.attribute");
            inetAddressValue = InetAddress.getByAddress(b);
        } catch (Exception e) {
        }
    }

    public void setValue(byte[] b, int off, int len) {
        if (b == null) return;
        try {
            if (!(len == 4 || len == 16)) {
                throw new RuntimeException("Wrong IP address size for a2.packet.attribute");
            }
            byte[] bf = new byte[len];
            System.arraycopy(b, off, bf, 0, len);
            inetAddressValue = InetAddress.getByAddress(bf);
        } catch (Exception e) {
        }
    }

    public String toString() {
        if (inetAddressValue != null) {
            return inetAddressValue.getHostAddress();
        }
        return "[Bad IP Address Value]";
    }

    public String toXMLString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<ip>");
        if (inetAddressValue != null) {
            sb.append(inetAddressValue.getHostAddress());
        }
        sb.append("</ip>");
        return sb.toString();
    }

    public void setInetAddress(InetAddress inet) {
        this.inetAddressValue = inet;
    }

    public Serializable getValueObject() {
        return inetAddressValue;
    }

    public void setValueObject(Serializable o) {
        if (o instanceof InetAddress) {
            setInetAddress((InetAddress) o);
        } else if (o instanceof byte[]) {
            setValue((byte[]) o);
        } else {
            try {
                setInetAddress(InetAddress.getByName(o.toString()));
            } catch (Exception e) {
                RadiusLog.warn(e.getMessage());
            }
        }
    }
}
