package a2.packet.attribute.value;

import a2.packet.attribute.A2Attribute;
import a2.packet.attribute.Format;

import java.nio.ByteBuffer;

public class TLVFormat extends Format
{
	int parentType;
	long vendorId;
	
	public TLVFormat(long vendor, int pt)
	{
		this.vendorId = vendor;
		this.parentType = pt;
	}

	/*
	@Override
	public void packAttribute(OutputStream out, RadiusAttribute a) throws IOException 
	{
        AttributeValue attributeValue = a.getValue();
        writeUnsignedByte(out, (int)a.getType());
        writeUnsignedByte(out, attributeValue.getLength() + 2);
        attributeValue.getBytes(out);
	}

	@Override
	public int unpackAttributeHeader(InputStream in, AttributeParseContext ctx) throws IOException 
	{
        ctx.attributeType = (readUnsignedByte(in) << 8) | (parentType & 0xFF);
        ctx.attributeLength = readUnsignedByte(in);
        ctx.vendorNumber = (int) vendorId;
        ctx.headerLength = 2;
		return 0;
	}
*/
	
	public void unpackAttributeHeader(ByteBuffer buffer, AttributeParseContext ctx) 
	{
        ctx.attributeType = (getUnsignedByte(buffer) << 8) | (parentType & 0xFF);
        ctx.attributeLength = getUnsignedByte(buffer);
        ctx.vendorNumber = (int) vendorId;
        ctx.headerLength = 2;
	}

	@Override
	public void packAttribute(ByteBuffer buffer, A2Attribute a)
	{
        AttributeValue attributeValue = a.getValue();
        putUnsignedByte(buffer, (int) a.getType());
        putUnsignedByte(buffer, attributeValue.getLength() + 2);
        attributeValue.getBytes(buffer);
	}
}
