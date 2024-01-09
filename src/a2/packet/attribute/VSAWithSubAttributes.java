package a2.packet.attribute;


/**
 * Sub-TLV a2.packet.attribute, as specified by WiMAX.
 * It is a VSA with a TLV type.
 * {@see VSAttribute#vsaAttributeType} is used as the TLV type.
 * <p>
 * It should be used when communicating with FreeRADIUS
 *
 * @author Danilo Levantesi <danilo.levantesi@witech.it>
 */
public abstract class VSAWithSubAttributes extends VSAttribute
{
    private static final long serialVersionUID = 0L;

    private AttributeList subAttributes = new AttributeList();

	protected long subTlvType;

    /**
     * Encode the sub-TLV type like FreeRADIUS does.
     * <p>
     * Returns the VSA type (lower 1 bytes) encoded with the Vendor ID
     * (upper 2 bytes) and the TLV type ("middle" byte). sub-TLV type cannot be
     * larger than 1 byte.
     * <pre>
     *  0                   1                   2
     *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |      Vendor-Id                |   TLV-Type    |   VSA-Type    |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * </pre>
     * @see net.jradius.packet.attribute.RadiusAttribute#getFormattedType()
     */
    public long getFormattedType()
    {
        return vsaAttributeType | (subTlvType << 8) | (vendorId << 16);
    }

    public long getSubTlvType() {
        return subTlvType;
    }

    public void setSubTlvType(long subTlvType) {
        this.subTlvType = subTlvType;
    }

 	public AttributeList getSubAttributes() {
		return subAttributes;
	}

	public void setSubAttributes(AttributeList subAttributes) {
		this.subAttributes = subAttributes;
	}
	
}
