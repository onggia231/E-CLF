package a2.exception;

import a2.packet.attribute.A2Attribute;
import a2.packet.attribute.AttributeFactory;

import java.util.Iterator;
import java.util.List;


/**
 * The Exception thrown by a RadiusStandard which found missing attributes.
 *
 * @author David Bird
 */
public class StandardViolatedException extends A2Exception
{
    private final Class standardClass;
    private final List missingAttributes;
    
    public StandardViolatedException(Class standardClass, List missing)
    {
        super("Standards Violation: " + standardClass.getName());
        this.standardClass = standardClass;
        this.missingAttributes = missing;
    }
    
    /**
     * @return Returns same as listAttribtues(", ")
     */
    public String listAttributes()
    {
    		return listAttributes(", ");
    }
    
    /**
     * Provides a listing of the names of the missing attributes
     * @param sep delimiter to use between a2.packet.attribute names
     * @return Returns the list of a2.packet.attribute names as a String
     */
    public String listAttributes(String sep)
    {
        StringBuffer sb = new StringBuffer();
        for (Iterator i = missingAttributes.iterator(); i.hasNext(); )
        {
            Long type = (Long)i.next();
            A2Attribute a = AttributeFactory.newAttribute(type.longValue(), null, false);
            if (a != null) sb.append(sep).append(a.getAttributeName());
        }
        return sb.substring(sep.length());
    }
    
    /**
     * @return Returns the Class the generated the a2.exception
     */
    public Class getStandardClass()
    {
        return standardClass;
    }
    
    /**
     * @return Returns the list of missing attributes (a list of Integers)
     */
    public List getMissingAttributes()
    {
        return missingAttributes;
    }
}
