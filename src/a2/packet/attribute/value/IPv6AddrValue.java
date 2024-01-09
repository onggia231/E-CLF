package a2.packet.attribute.value;

import java.net.InetAddress;

/**
 * The IPv6 a2.packet.attribute value
 *
 * @author David Bird
 */
public class IPv6AddrValue extends IPAddrValue
{
    private static final long serialVersionUID = 0L;

    public IPv6AddrValue() { }
    
    public IPv6AddrValue(InetAddress i)
    {
        super(i);
    }

    public int getLength()
    {
        return 16;
    }
}
