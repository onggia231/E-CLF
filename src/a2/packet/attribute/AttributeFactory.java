package a2.packet.attribute;

import a2.exception.A2Exception;
import a2.exception.UnknownAttributeException;
import a2.log.RadiusLog;
import a2.packet.A2Format;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * The Attribute Factor. This factor builds the RADIUS attributes
 * based on configured dictionaries.
 *
 * @author David Bird
 */
public final class AttributeFactory {
    private static LinkedHashMap<Long, Class<?>> attributeMap = new LinkedHashMap<Long, Class<?>>();
    private static LinkedHashMap<Long, Class<?>> vendorMap = new LinkedHashMap<Long, Class<?>>();
    private static LinkedHashMap<Long, VendorValue> vendorValueMap = new LinkedHashMap<Long, VendorValue>();
    private static LinkedHashMap<String, Class<?>> attributeNameMap = new LinkedHashMap<String, Class<?>>();
    private static KeyedObjectPool attributeObjectPool = new AttributeFactoryPool();

    private static A2Attribute vsa(long vendor, long type) throws InstantiationException, IllegalAccessException {
        A2Attribute attr = null;
        VendorValue v = vendorValueMap.get(new Long(vendor));
        Class<?> c = null;

        if (v != null) {
            c = v.typeMap.get(new Long(type));
        }

        if (c != null) {
            attr = (A2Attribute) c.newInstance();
        } else {
            RadiusLog.warn("Unknown Vendor Specific Attribute: " + vendor + ":" + type);
            attr = new Attr_UnknownVSAttribute(vendor, type);
        }

        return attr;
    }

    private static A2Attribute attr(long type) throws InstantiationException, IllegalAccessException {
        A2Attribute attr = null;
        Class<?> c = attributeMap.get(new Long(type));

        if (c != null) {
            attr = (A2Attribute) c.newInstance();
        } else {
            RadiusLog.warn("Unknown Attribute: " + type);
            attr = new Attr_UnknownAttribute(type);
        }

        return attr;
    }

    public static A2Attribute newAttribute(Long key) throws Exception {
        A2Attribute a = null;

        long val = key.longValue();
        long vendor = val >> 16;
        long type = val & 0xFFFF;

        if (vendor != 0) {
            a = vsa(vendor, type);
        } else {
            a = attr(type);
        }

        // System.err.println("Created "+a.toString() + " " + key + " " + a.getFormattedType());

        return a;
    }

    public static A2Attribute newAttribute(Long key, Serializable value, boolean pool) {
        A2Attribute attr = null;

        try {
            if (pool) {
                attr = borrow(key);
            }

            if (attr == null) {
                attr = newAttribute(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        attr.getValue().setValueObject(value);

        return attr;
    }

    public static A2Attribute copyAttribute(A2Attribute a) {
        return copyAttribute(a, true);
    }

    public static A2Attribute copyAttribute(A2Attribute a, boolean pool) {
        Long key = new Long(a.getFormattedType());
        A2Attribute attr = null;

        try {
            if (pool) {
                attr = borrow(key);
            }

            if (attr == null) {
                attr = newAttribute(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        attr.getValue().copy(a.getValue());

        return attr;
    }

    public static A2Attribute borrow(Long key) throws NoSuchElementException, IllegalStateException, Exception {
        A2Attribute attr = null;

        if (attributeObjectPool != null) {
            attr = (A2Attribute) attributeObjectPool.borrowObject(key);
            // System.err.println("Borrowed "+attr.toString() + " " + key + " " + attr.getFormattedType());
        }

        return attr;
    }

    /**
     * Load an a2.packet.attribute dictionary
     *
     * @param className Name of the Java Class derived from AttributeDictionary
     * @return Returns true if loading of dictionary was successful
     */
    public static boolean loadAttributeDictionary(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            Object o = clazz.newInstance();
            return loadAttributeDictionary((AttributeDictionary) o);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void loadA2Dictionary() {
        AttributeFactory.attributeMap = new LinkedHashMap<Long, Class<?>>();
        AttributeFactory.attributeMap.put(0x01L, Attr_IPAddress.class);
        AttributeFactory.attributeMap.put(0x02L, Attr_TransactionId.class);
        AttributeFactory.attributeMap.put(0x06L, Attr_CallingLineIdentification.class);
        AttributeFactory.attributeMap.put(0x07L, Attr_IPAddressingZone.class);
        AttributeFactory.attributeMap.put(0x08L, Attr_TerminalType.class);
        AttributeFactory.attributeMap.put(0x09L, Attr_ErrorCode.class);
        AttributeFactory.attributeMap.put(0x0AL, Attr_NetworkType.class);
        AttributeFactory.attributeMap.put(0x0BL, Attr_FormattedRemoteId.class);
    }

    public static boolean loadAttributeDictionary(AttributeDictionary dict) {
        dict.loadAttributes(attributeMap);
        dict.loadAttributesNames(attributeNameMap);
        dict.loadVendorCodes(vendorMap);

        Iterator<Long> i = vendorMap.keySet().iterator();
        while (i.hasNext()) {
            Long id = i.next();
            Class<?> c = vendorMap.get(id);
            try {
                LinkedHashMap<Long, Class<?>> typeMap = new LinkedHashMap<Long, Class<?>>();
                LinkedHashMap<String, Class<?>> nameMap = new LinkedHashMap<String, Class<?>>();
                VSADictionary vsadict = (VSADictionary) c.newInstance();
                vsadict.loadAttributes(typeMap);
                vsadict.loadAttributesNames(nameMap);
                vsadict.loadAttributesNames(attributeNameMap);
                vendorValueMap.put(id, new VendorValue(c, typeMap, nameMap));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * Parses a string to create a RadiusAttribute. Will either return the
     * a2.packet.attribute, or throw an Exception.
     *
     * @param src The source String
     * @return Returns the RadiusAttribute parsed from String
     * @throws A2Exception
     * @throws UnknownAttributeException
     */
    public static A2Attribute attributeFromString(String src) throws A2Exception, UnknownAttributeException {
        String parts[] = src.split("=", 2);

        if (parts.length == 2) {
            String attribute = parts[0].trim();
            String value = parts[1].trim();

            char q = value.charAt(0);
            if (q == value.charAt(value.length() - 1) && (q == '\'' || q == '"')) {
                value = value.substring(1, value.length() - 1);
            }

            return newAttribute(attribute, value, "=");
        }

        throw new A2Exception("Syntax error for attributes: " + src);
    }

    public static void loadAttributesFromString(AttributeList list, String src, String delim, boolean beStrinct) throws A2Exception {
        StringTokenizer st = new StringTokenizer(src, delim);
        while (st.hasMoreTokens()) {
            try {
                list.add(attributeFromString(st.nextToken()));
            } catch (A2Exception e) {
                if (beStrinct) throw (e);
            }
        }
    }

    /**
     * Creates a new RadiusAttribute
     *
     * @param vendor The VendorID of the a2.packet.attribute (if one)
     * @param type   The Attribute Type
     * @param value  The Attribute Value
     * @param op     The Attribute Operator
     * @return Returns the newly created RadiusAttribute
     */
    public static A2Attribute newAttribute(long vendor, long type, byte[] value, int op, boolean pool) {
        A2Attribute attr = null;

        try {
            if (vendor > 1 || type == 26) {
                boolean onWire = (vendor < 1);
                DataInputStream input = null;

                if (onWire) {
                    /*
                     *  We are parsing an off-the-wire packet
                     */
                    ByteArrayInputStream bais = new ByteArrayInputStream(value);
                    input = new DataInputStream(bais);

                    vendor = A2Format.readUnsignedInt(input);
                    type = A2Format.readUnsignedByte(input);
                }

                Long key = new Long(vendor << 16 | type);

                if (pool) {
                    attr = borrow(key);
                }

                if (attr == null) {
                    attr = vsa(vendor, type);
                }

                if (onWire) {
                    VSAttribute vsa = (VSAttribute) attr;
                    int vsaLength = 0;
                    int vsaHeaderLen = 2;
                    switch (vsa.getLengthLength()) {
                        case 1:
                            vsaLength = A2Format.readUnsignedByte(input);
                            break;
                        case 2:
                            vsaLength = A2Format.readUnsignedShort(input);
                            vsaHeaderLen++;
                            break;
                        case 4:
                            vsaLength = (int) A2Format.readUnsignedInt(input);
                            vsaHeaderLen += 3;
                            break;
                    }
                    if (vsa.hasContinuationByte) {
                        vsa.continuation = (short) A2Format.readUnsignedByte(input);
                        vsaHeaderLen++;
                    }
                    byte[] newValue = new byte[vsaLength - vsaHeaderLen];
                    input.readFully(newValue);
                    input.close();
                    value = newValue;
                }
            } else {
                if (pool) {
                    attr = borrow(type);
                }

                if (attr == null) {
                    attr = attr(type);
                }
            }

            if (value != null) attr.setValue(value);
            else attr.setValue(new byte[]{});
            if (op > -1) attr.setAttributeOp(op);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return attr;
    }

    public static A2Attribute newAttribute(long vendor, long type, long len, int op, ByteBuffer buffer, boolean pool) {
        A2Attribute attr = null;

        int valueLength = (int) len;

        try {
            if (vendor > 1 || type == 26) {
                boolean needVendorAndType = (vendor < 1);
                boolean needVendorType = (type < 1);

                if (needVendorAndType) {
                    vendor = A2Format.getUnsignedInt(buffer);
                }
                if (needVendorAndType || needVendorType) {
                    type = A2Format.getUnsignedByte(buffer);
                }

                Long key = new Long(vendor << 16 | type);

                if (pool) {
                    attr = borrow(key);
                }

                if (attr == null) {
                    attr = vsa(vendor, type);
                }

                if (needVendorAndType || needVendorType) {
                    VSAttribute vsa = (VSAttribute) attr;
                    int vsaLength = 0;
                    int vsaHeaderLen = 2;

                    switch (vsa.getLengthLength()) {
                        case 1:
                            vsaLength = A2Format.getUnsignedByte(buffer);
                            break;
                        case 2:
                            vsaLength = A2Format.getUnsignedShort(buffer);
                            vsaHeaderLen++;
                            break;
                        case 4:
                            vsaLength = (int) A2Format.getUnsignedInt(buffer);
                            vsaHeaderLen += 3;
                            break;
                    }

                    if (vsa.hasContinuationByte) {
                        vsa.continuation = (short) A2Format.getUnsignedByte(buffer);
                        vsaHeaderLen++;
                    }

                    valueLength = vsaLength - vsaHeaderLen;
                }
            } else {
                if (pool) {
                    attr = borrow(type);
                }

                if (attr == null) {
                    attr = attr(type);
                }
            }

            if (valueLength > 0) {
                attr.setValue(buffer.array(), buffer.position(), valueLength);
                buffer.position(buffer.position() + valueLength);
            } else {
                attr.setValue(null, 0, 0);
            }

            if (op > -1) {
                attr.setAttributeOp(op);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return attr;
    }

    /**
     * Creates a new RadiusAttribute
     *
     * @param type  The type of the a2.packet.attribute
     * @param value The value of the a2.packet.attribute
     * @return Returns the newly created RadiusAttribute
     */
    public static A2Attribute newAttribute(long type, byte[] value, boolean pool) {
        return newAttribute((type >> 16), type & 0xFFFF, value, -1, pool);
    }

    /**
     * @param type  The type of the a2.packet.attribute
     * @param value The value of the a2.packet.attribute
     * @return Returns the newly created AttributeList
     */
    public static AttributeList newAttributeList(long type, byte[] value, boolean pool) {
        AttributeList list = new AttributeList();
        addToAttributeList(list, type, value, pool);
        return list;
    }

    /**
     * @param list  The AttributeList to add to
     * @param type  The type of the a2.packet.attribute
     * @param value The value of the a2.packet.attribute
     * @return Returns how many attributes created
     */
    public static int addToAttributeList(AttributeList list, long type, byte[] value, boolean pool) {
        int left = (value == null) ? 0 : value.length;
        int offset = 0;
        int cnt = 0;

        long vendor = (type >> 16);
        int maxlen = vendor > 0 ? 247 : 253;
        type = type & 0xFF;

        while (left > 0) {
            int len = maxlen;
            if (left < maxlen) len = left;
            byte b[] = new byte[len];
            System.arraycopy(value, offset, b, 0, len);
            list.add(AttributeFactory.newAttribute(vendor, type, b, A2Attribute.Operator.ADD, pool), false);
            offset += len;
            left -= len;
            cnt++;
        }
        return cnt;
    }

    public static byte[] assembleAttributeList(AttributeList list, long type) {
        Object[] aList;
        A2Attribute a;

        aList = list.getArray(type);

        if (aList != null) {
            int length = 0;
            for (int i = 0; i < aList.length; i++) {
                a = (A2Attribute) aList[i];
                byte[] b = a.getValue().getBytes();
                if (b != null) length += b.length;
            }

            byte[] byteBuffer = new byte[length];

            int offset = 0;
            for (int i = 0; i < aList.length; i++) {
                a = (A2Attribute) aList[i];
                byte[] b = a.getValue().getBytes();
                System.arraycopy(b, 0, byteBuffer, offset, b.length);
                offset += b.length;
            }

            return byteBuffer;
        }

        return null;
    }

    /**
     * Create a RadiusAttribute by name
     *
     * @param aName The name of the a2.packet.attribute to create
     * @return Returns the newly created RadiusAttribute
     * @throws UnknownAttributeException
     */
    public static A2Attribute newAttribute(String aName) throws UnknownAttributeException {
        Class<?> c = attributeNameMap.get(aName);
        A2Attribute attr = null;

        if (c == null) {
            throw new UnknownAttributeException("Unknown a2.packet.attribute " + aName);
        }

        try {
            attr = (A2Attribute) c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return attr;
    }

    /**
     * Create a new RadiusAttribute based on a AttributeDescription
     *
     * @param desc The RadiusDescription
     * @return Returns the newly created RadiusAttribute
     * @throws UnknownAttributeException
     */
    public static A2Attribute newAttribute(AttributeDescription desc) throws UnknownAttributeException {
        return newAttribute(desc.getName(), desc.getValue(), desc.getOp());
    }

    /**
     * Creates a new RadiusAttribute
     *
     * @param aName  The name of the a2.packet.attribute to create
     * @param aValue The value of the a2.packet.attribute
     * @param aOp    The "operator" of the a2.packet.attribute
     * @return Returns the newly created RadiusAttribute
     * @throws UnknownAttributeException
     */
    public static A2Attribute newAttribute(String aName, String aValue, String aOp) throws UnknownAttributeException {
        A2Attribute attr = newAttribute(aName);
        attr.setAttributeOp(aOp);
        attr.setValue(aValue);
        return attr;
    }

    /**
     * The the integer type of a RadiusAttribute by name
     *
     * @param aName The name of the a2.packet.attribute
     * @return Returns the integer type of the a2.packet.attribute
     * @throws UnknownAttributeException
     */
    public static long getTypeByName(String aName) throws UnknownAttributeException {
        Class<?> c = attributeNameMap.get(aName);
        A2Attribute attr = null;

        if (c == null) {
            throw new UnknownAttributeException("Unknown a2.packet.attribute " + aName);
        }

        try {
            attr = (A2Attribute) c.newInstance();
            return attr.getFormattedType();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * @return Returns the attributeMap.
     */
    public static LinkedHashMap<Long, Class<?>> getAttributeMap() {
        return attributeMap;
    }

    /**
     * @return Returns the attributeNameMap.
     */
    public static LinkedHashMap<String, Class<?>> getAttributeNameMap() {
        return attributeNameMap;
    }

    /**
     * @return Returns the vendorMap.
     */
    public static LinkedHashMap<Long, Class<?>> getVendorMap() {
        return vendorMap;
    }

    /**
     * @return Returns the vendorValueMap.
     */
    public static LinkedHashMap<Long, VendorValue> getVendorValueMap() {
        return vendorValueMap;
    }

    public static void poolStatus() {
        if (attributeObjectPool == null) return;
        System.err.println("AttributePool: active=" + attributeObjectPool.getNumActive() + " idle=" + attributeObjectPool.getNumIdle());
    }

    public static String getPoolStatus() {
        if (attributeObjectPool == null) return "";
        return "active=" + attributeObjectPool.getNumActive() + ", idle=" + attributeObjectPool.getNumIdle();
    }

    public static void recycle(A2Attribute a) {
        if (attributeObjectPool == null || !a.recyclable) {
            // System.err.println("Did not recycle " + a.toString());
            return;
        }

        if (a.recycled) {
            System.err.println("PROBLEM: Recycling " + a.toString() + " " + a.getFormattedType());
        }

        a.setOverflow(false);

        try {
            if (a instanceof VSAWithSubAttributes) {
                VSAWithSubAttributes aa = (VSAWithSubAttributes) a;
                AttributeList list = aa.getSubAttributes();
                list.clear();
            }

            attributeObjectPool.returnObject(new Long(a.getFormattedType()), a);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void recycle(AttributeList list) {
        synchronized (list) {
            for (A2Attribute a : list.getAttributeList()) {
                recycle(a);
            }
        }

        // poolStatus();
    }

    public static class AttributeFactoryPool extends GenericKeyedObjectPool {
        public AttributeFactoryPool() {
            super(new KeyedPoolableObjectFactory() {
                public boolean validateObject(Object arg0, Object arg1) {
                    return true;
                }

                public void passivateObject(Object arg0, Object arg1) throws Exception {
                    A2Attribute a = (A2Attribute) arg1;
                    a.recycled = true;
                }

                public Object makeObject(Object arg0) throws Exception {
                    A2Attribute a = newAttribute((Long) arg0);
                    a.recyclable = true;
                    a.recycled = false;
                    return a;
                }

                public void destroyObject(Object arg0, Object arg1) throws Exception {
                }

                public void activateObject(Object arg0, Object arg1) throws Exception {
                    A2Attribute a = (A2Attribute) arg1;
                    a.recycled = false;
                }

            });

            setMaxActive(-1);
            setMaxIdle(-1);
        }
    }

    public static final class VendorValue {
        private Class<?> c;
        private Map<Long, Class<?>> typeMap;
        private Map<String, Class<?>> nameMap;

        public VendorValue(Class<?> c, LinkedHashMap<Long, Class<?>> t, Map<String, Class<?>> n) {
            this.c = c;
            typeMap = t;
            nameMap = n;
        }

        public Map<String, Class<?>> getAttributeNameMap() {
            return nameMap;
        }

        public Map<Long, Class<?>> getAttributeMap() {
            return typeMap;
        }

        public Class<?> getDictClass() {
            return c;
        }
    }
}
