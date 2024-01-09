package a2.packet.attribute.value;

/**
 * The Enrypted String a2.packet.attribute value
 *
 * @author David Bird
 */
public class EncryptedStringValue extends OctetsValue
{
    private static final long serialVersionUID = 0L;

    public EncryptedStringValue() { }
    
    public EncryptedStringValue(String s)
    {
        super((s != null) ? s.getBytes() : null);
    }
    
    public EncryptedStringValue(byte[] b)
    {
        super(b);
    }
    
    public String toString()
    {
        return "[Encrypted String]";
    }
}
