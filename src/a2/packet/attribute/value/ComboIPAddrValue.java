package a2.packet.attribute.value;

import java.net.InetAddress;

/**
 * WiMAX combo-ip a2.packet.attribute.
 * <p>
 * If length 4, is the same as the {@code ipaddr} type.
 * If length 16, is the same as {@code ipv6addr} type.
 *
 * @author Danilo Levantesi <danilo.levantesi@witech.it>
 */
public class ComboIPAddrValue extends IPAddrValue {
    private static final long serialVersionUID = 0L;

    public ComboIPAddrValue() {
    }

    public ComboIPAddrValue(InetAddress i) {

    }
}
