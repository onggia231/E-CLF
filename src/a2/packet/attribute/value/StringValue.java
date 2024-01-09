package a2.packet.attribute.value;

import java.io.Serializable;

/**
 * The String a2.packet.attribute value
 *
 * @author David Bird
 */
public class StringValue extends OctetsValue
{
    private static final long serialVersionUID = 0L;
    
    public StringValue() { }
    
    public StringValue(String s)
    {
        byteValue = s.getBytes();
    }
    
    public String toString()
    {
        if (byteValue == null) return null;
        String stringValue = new String(byteValue, byteValueOffset, byteValueLength);
        return stringValue.trim();
    }
    
    public String toXMLString()
    {
        String s = toString();
        StringBuffer sb = new StringBuffer();
        sb.append("<string>").append(s == null ? "" : s).append("</string>");
        return sb.toString();
    }

    public Serializable getValueObject()
    {
        if (byteValue == null) return null;
        return new String(byteValue, byteValueOffset, byteValueLength);
    }

    public void setString(String s)
    {
        setValue(s.getBytes());
    }

    public void setValueObject(Serializable o)
    {
		if (o instanceof byte[])
		{
			super.setValueObject(o);
		}
		else
		{
			setString(o.toString());
		}
    }
}
