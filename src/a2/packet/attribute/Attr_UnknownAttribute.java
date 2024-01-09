package a2.packet.attribute;

import a2.packet.attribute.value.OctetsValue;

/**
 * @author David Bird
 */
public class Attr_UnknownAttribute extends A2Attribute implements UnknownAttribute
{
    private static final long serialVersionUID = 0L;
    public static final String NAME = "Unknown-Attribute";

    public void setup() {}

    public void setup(long type)
    {
        attributeName = NAME + "(" + type + ")";
        attributeType = type;
    }

    public Attr_UnknownAttribute(long type)
    {
        setup(type);
        attributeValue = new OctetsValue();
    }

    public Attr_UnknownAttribute(long type, OctetsValue v)
    {
        setup(type);
        attributeValue = v;
    }

    public Attr_UnknownAttribute(long type, byte[]  v)
    {
        setup(type);
        attributeValue = new OctetsValue(v);
    }
    
    public long getAttributeType() 
    {
        return attributeType;
    }

    public void setAttributeName(String attributeName)
    {
    		this.attributeName = attributeName;
    }
}
