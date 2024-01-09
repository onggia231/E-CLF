package a2.packet.attribute.value;

import a2.packet.attribute.Format;
import a2.log.RadiusLog;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * The Integer a2.packet.attribute value. Uses a Long as the underlying object since
 * this is an unsigned long in RADIUS. 
 *
 * @author David Bird
 */
public class IntegerValue extends AttributeValue
{
    private static final long serialVersionUID = 0L;
    protected int length = 4;
    protected Long integerValue;
    
    public IntegerValue() { }
    
    public IntegerValue(Long l)
    {
        integerValue = l;
    }
    
    public IntegerValue(Integer i)
    {
        setValue(i.longValue());
    }
    
    public IntegerValue(int i)
    {
        setValue(i);
    }
    
    public IntegerValue(long l)
    {
        setValue(l);
    }

    public void copy(AttributeValue value)
    {
    	IntegerValue iValue = (IntegerValue) value;
    	this.integerValue = iValue.integerValue;
    	this.length = iValue.length;
    }
    
    public int getLength()
    {
        return length;
    }
    
    public void setLength(int length)
    {
        this.length = length;
    }
    
    public void getBytes(OutputStream out) throws IOException
    {
        if (integerValue != null)
        {
            long longValue = integerValue.longValue();
            
            if (length == 4)
            {
                out.write((int)((longValue >> 24) & 0xFF));
                out.write((int)((longValue >> 16) & 0xFF));
            }
            
            if (length >= 2)
            {
                out.write((int)((longValue >>  8) & 0xFF));
            }

            out.write((int)(longValue & 0xFF));
        }
    }

    public void getBytes(ByteBuffer buffer)
    {
        if (integerValue != null)
        {
            long longValue = integerValue.longValue();
            
            if (length == 4)
            {
            	Format.putUnsignedByte(buffer, (int)((longValue >> 24) & 0xFF));
            	Format.putUnsignedByte(buffer, (int)((longValue >> 16) & 0xFF));
            }
            
            if (length >= 2)
            {
            	Format.putUnsignedByte(buffer, (int)((longValue >> 8) & 0xFF));
            }

        	Format.putUnsignedByte(buffer, (int)(longValue & 0xFF));
        }
    }

    public void setValue(byte[] b)
    {
        if (b == null) return;
    	setValue(b, 0, b.length);
    }
    
    public void setValue(byte[] b, int off, int len)
    {
        if (b == null) return;
        try
        {
        	switch (len)
            {
                case 1: // it's really a byte
                {
                    length = 1;
                    integerValue = new Long((int)b[off]&0xFF);
                }
                break;
                
                case 2:
                {
                    length = 2;
                    long longValue = 
                        (long)((int)b[off] & 0xFF) <<  8 | 
                        (long)((int)b[off + 1] & 0xFF);
        
                    integerValue = new Long(longValue);
                }
                break;

                case 4:
                {
                    long longValue = 
                        (long)((int)b[off] & 0xFF) << 24 | 
                        (long)((int)b[off + 1] & 0xFF) << 16 | 
                        (long)((int)b[off + 2] & 0xFF) <<  8 | 
                        (long)((int)b[off + 3] & 0xFF);
        
                    integerValue = new Long(longValue);
                }
                break;
            }
        }
        catch (Exception e)
        {
            RadiusLog.warn("Error during bean initialization [InitializingBean]", e);
        }
    }
    
    public void setValue(String v)
    {
        setValue(Long.parseLong(v));
    }
    
    public Long getValue()
    {
        return integerValue;
    }
    
    public String toString()
    {
        if (integerValue != null)
        {
            return integerValue.toString();
        }
        return "[Bad Integer Value]";
    }
    
    public String toXMLString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<int>");
        if (integerValue != null) 
        {
            sb.append(integerValue);
        }
        sb.append("</int>");
        return sb.toString();
    }

    public void setLong(Long l)
    {
        this.integerValue = l;
    }
    
    public Serializable getValueObject()
    {
        return integerValue;
    }
    
    public void setValueObject(Serializable o)
    {
		if (o instanceof Long)
		{
			setLong((Long)o);
		}
		else if (o instanceof Number)
		{
			setLong(new Long(((Number)o).longValue()));
		}
		else
		{
			setLong(new Long(Long.parseLong(o.toString())));
		}
    }
    
    public void setValue(long l) throws NumberFormatException
    {
        if (isValid(l) == false) throw new NumberFormatException("[bad unsigned integer value: " + String.valueOf(l) + "]");
        integerValue = new Long(l);
    }

    public static boolean isValid(long l)
    {
        if ((l < 0L) || (l > 4294967295L)) return false;
        return true;
    }
}
