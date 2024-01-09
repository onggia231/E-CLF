package a2.packet.attribute;

import java.util.Map;

/**
 * Attribute Dictionary Interface. Attribute dictionary classes, like
 * that built RadiusDictionary, implement this interface.
 *
 * @author David Bird
 */
public abstract interface AttributeDictionary
{
    // Some commonly used standard RADIUS Attribute types.
    // Of course, a dictionary supporing them must also be loaded.
    // Values are added as they are used in the jradius package (which
    // should not be referencing any a2.packet.attribute class directly).
    public static final int USER_NAME 				= 1;	// User-Name
    public static final int USER_PASSWORD 			= 2;	// User-Password
    public static final int STATE					= 24;	// State
    public static final int CLASS					= 25;   // Class
    public static final int NAS_IDENTIFIER			= 32;	// NAS-Identifier
    public static final int ACCT_STATUS_TYPE 		= 40;	// Acct-Status-Type
    public static final int EAP_MESSAGE				= 79;	// EAP-Message
    public static final int MESSAGE_AUTHENTICATOR	= 80;	// Message-Authenticator
    public static final int CHARGEABLE_USER_IDENTITY= 89;	// Message-Authenticator
    
    public void loadVendorCodes(Map<Long, Class<?>> map);
    public void loadAttributes(Map<Long, Class<?>> map);
    public void loadAttributesNames(Map<String, Class<?>> map);
}
