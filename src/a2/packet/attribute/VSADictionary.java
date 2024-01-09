package a2.packet.attribute;

import java.util.Map;

/**
 * VSA Attribute Dictionary Interface. Vendor specific a2.packet.attribute dictionary classes,
 * like that built RadiusDictionary, implement this interface.
 *
 * @author David Bird
 */
public abstract interface VSADictionary
{
    public String getVendorName();
    public void loadAttributes(Map<Long, Class<?>> map);
    public void loadAttributesNames(Map<String, Class<?>> map);
}
