package a2.packet.attribute.value;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * The Date a2.packet.attribute value
 *
 * @author David Bird
 */
public class DateValue extends IntegerValue
{
    private static final long serialVersionUID = 0L;
    private Date dateValue;
    
    public DateValue() { }
    
    public DateValue(Date d)
    {
        dateValue = d;
    }
    
    public void copy(AttributeValue value)
    {
    	DateValue dValue = (DateValue) value;
    	this.integerValue = dValue.integerValue;
    	this.length = dValue.length;
    	this.dateValue = dValue.dateValue;
    }
    
    public void getBytes(OutputStream out) throws IOException
    {
        integerValue = new Long(dateValue.getTime() / 1000);
        super.getBytes(out);
    }
    
	public void setValue(byte[] b, int off, int len) 
	{
		super.setValue(b, off, len);
        dateValue = new Date(integerValue.longValue() * 1000);
	}

	public void setValue(byte[] b)
    {
        super.setValue(b);
        dateValue = new Date(integerValue.longValue() * 1000);
    }

    public void setValue(int i)
    {
        super.setValue(i);
        dateValue = new Date(integerValue.longValue() * 1000);
    }
    
    public void setValue(long l)
    {
        super.setValue(l);
        dateValue = new Date(integerValue.longValue() * 1000);
    }
    
	public void setValue(String v) 
	{
		super.setValue(v);
        dateValue = new Date(integerValue.longValue() * 1000);
	}

	public void setLong(Long l) 
	{
		super.setLong(l);
        dateValue = new Date(integerValue.longValue() * 1000);
	}

	public void getBytes(ByteBuffer buffer)
    {
	    integerValue = new Long(dateValue.getTime() / 1000);
	    super.getBytes(buffer);
    }
 
    public String toString()
    {
        if (dateValue != null)
        {
            return dateValue.toString();
        }
        return "[Bad Date Value]";
    }
    
    public String toXMLString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<time>");
        if (dateValue != null) 
        {
            sb.append(dateValue.getTime());
        }
        sb.append("</time>");
        return sb.toString();
    }

    public void setDate(Date newDate)
    {
        this.dateValue = newDate;
        this.integerValue = new Long(this.dateValue.getTime() / 1000);
    }
    
    public Serializable getValueObject()
    {
        return dateValue;
    }

    public void setValueObject(Serializable o)
    {
        if (o instanceof Date)
        {
            setDate((Date)o);
        }
        else if (o instanceof Number)
        {
            setDate(new Date(((Number)o).longValue() * 1000));
        }
        else
        {
            setDate(new Date((Long.parseLong(o.toString())) * 1000));
        }
    }
}
