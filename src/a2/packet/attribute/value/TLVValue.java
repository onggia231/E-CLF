package a2.packet.attribute.value;


import a2.packet.attribute.AttributeList;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class TLVValue extends OctetsValue
{
	private static final long serialVersionUID = 1L;
	private TLVFormat format;
	private AttributeList list;
	
	public TLVValue(long vendorId, int vsaType, AttributeList subAttributes) {
		format = new TLVFormat(vendorId, vsaType);
		list = subAttributes;
	}
	
	@Override
	public void getBytes(ByteBuffer buffer) {
		format.packAttributeList(list, buffer, false);
	}

	
    @Override
	public void copy(AttributeValue value) 
    {
    	TLVValue tlvValue = (TLVValue) value;
    	list.clear();
    	list.add(tlvValue.list);
	}

	@Override
	public int getLength() {
		//XXX
		ByteBuffer b = ByteBuffer.allocate(4096);
		format.packAttributeList(list, b, true);
		return b.position();
	}

	@Override
	public Serializable getValueObject() {
		return super.getValueObject();
	}

	@Override
	public void setValue(byte[] b) {
		list.clear();
		if (b != null && b.length > 0)
		{
			ByteBuffer bb = ByteBuffer.wrap(b);
			format.unpackAttributes(list, bb, bb.limit(), false);
		}
	}

	@Override
    public void setValue(byte[] b, int off, int len) {
		list.clear();
		if (b != null && len > 0)
		{
			ByteBuffer bb = ByteBuffer.wrap(b, off, len);
			format.unpackAttributes(list, bb, len, false);
		}
    }
    
	@Override
	public void setValueObject(Serializable o) 
	{
		super.setValueObject(o);
	}

	@Override
    public String toDebugString()
    {
    	return "["+list.toString().trim().replaceAll("\n", ", ")+"]";
    }

	@Override
    public String toString()
    {
    	return toDebugString();
    }
}
