package a2.packet.attribute;

import a2.packet.attribute.value.AttributeValue;
import a2.exception.UnknownAttributeException;

import java.io.Serializable;
import java.util.*;

/**
 * Represents the Attribute List of a packet. Supports singleton
 * and lists of a2.packet.attribute values (building packets with several
 * of the same a2.packet.attribute).
 *
 * @author David Bird
 */
public class AttributeList implements Serializable {
    private static final long serialVersionUID = 0L;
    private LinkedList<A2Attribute> attributeOrderList;
    private Map<Long, Object> attributeMap;

    /**
     * Default constructor
     */
    public AttributeList() {
        attributeMap = new LinkedHashMap<Long, Object>();
        attributeOrderList = new LinkedList<A2Attribute>();
    }

    /**
     * Add an a2.packet.attribute list to this a2.packet.attribute list
     *
     * @param list The a2.packet.attribute list to add
     */
    public void add(AttributeList list) {
        copy(list, true);
    }

    public void copy(AttributeList list, boolean pool) {
        if (list != null) {
            for (A2Attribute a : list.getAttributeList()) {
                _add(AttributeFactory.copyAttribute(a, pool), false);
            }
        }
    }

    /**
     * Add an a2.packet.attribute, defaulting to overwriting
     *
     * @param a The a2.packet.attribute to add
     */
    public void add(A2Attribute a) {
        add(a, true);
    }

    @SuppressWarnings("unchecked")
    public void _add(A2Attribute a, boolean overwrite) {
        Long key = new Long(a.getFormattedType());

        Object o = attributeMap.get(key);
        attributeOrderList.add(a);

        if (o == null || overwrite) {
            remove(key);
            attributeMap.put(key, a);
        } else {
            // If we already have this a2.packet.attribute and are not
            // overwriting, then we create a list of attributes.
            if (o instanceof LinkedList) {
                ((LinkedList) o).add(a);
            } else {
                LinkedList l = new LinkedList();
                l.add(o);
                l.add(a);
                attributeMap.put(key, l);
            }
        }
    }

    /**
     * Add an a2.packet.attribute with option to overwrite. If overwrite is false,
     * multiple of the same a2.packet.attribute can be added (building a list)
     *
     * @param a
     * @param overwrite
     */
    public void add(A2Attribute a, boolean overwrite) {
        if (a instanceof SubAttribute) {
            SubAttribute subAttribute = (SubAttribute) a;

            try {
                A2Attribute _pAttribute = (A2Attribute) subAttribute.getParentClass().newInstance();
                A2Attribute pAttribute = (A2Attribute) get(_pAttribute.getFormattedType(), true);

                if (pAttribute == null) {
                    pAttribute = _pAttribute;
                    add(pAttribute);
                }

                ((VSAWithSubAttributes) pAttribute).getSubAttributes()._add(a, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            _add(a, overwrite);
        }
    }

    /**
     * Removes a2.packet.attribute(s) by type
     * @param a RadiusAttribute to remove
     */
    public void remove(A2Attribute a) {
        if (a != null)
            remove(a.getFormattedType());
    }

    /**
     * Removes a2.packet.attribute(s) by type
     * @param attributeType The a2.packet.attribute type to remove
     */
    public void remove(long attributeType) {
        Long key = new Long(attributeType);
        Object o = attributeMap.remove(key);
        if (o != null) {
            if (o instanceof LinkedList<?>) {
                for (Iterator<?> i = ((LinkedList<?>) o).iterator(); i.hasNext(); ) {
                    removeFromList(i.next());
                }
            } else {
                removeFromList(o);
            }
        }
    }

    public void clear() {
        AttributeFactory.recycle(this);

        attributeMap.clear();
        attributeOrderList.clear();
    }

    private void removeFromList(Object o) {
        // System.err.println("removing "+o.toString());
        Object ol[] = attributeOrderList.toArray();
        for (int i = 0; i < ol.length; i++) {
            if (ol[i] == o) {
                attributeOrderList.remove(i);
                if (ol[i] instanceof A2Attribute) {
                    AttributeFactory.recycle((A2Attribute) ol[i]);
                }
                return;
            }
        }
    }

    /**
     * @return Returns the number of attributes in the list
     */
    public int getSize() {
        return attributeOrderList.size();
    }

    /**
     * Removes all unknown (not in the configured JRadius Dictionary) attribtues.
     */
    public void removeUnknown() {
        List<A2Attribute> list = getAttributeList();
        for (A2Attribute a : list) {
            if (a instanceof UnknownAttribute) {
                remove(a);
            }
        }
    }

    /**
     * @param type The type of a2.packet.attribute to retrieve
     * @param single True if a only a single a2.packet.attribute can be returned;
     * false if a List of attributes is also ok
     * @return Returns either s single a2.packet.attribute, a list of attributes, or null
     */
    @SuppressWarnings("unchecked")
    public Object get(long type, boolean single) {
        Long key = new Long(type);
        Object o = attributeMap.get(key);
        if (o == null || !(o instanceof LinkedList)) {
            return o;
        }
        LinkedList<Object> l = (LinkedList<Object>) o;
        return (single ? l.get(0) : o);
    }

    public A2Attribute get(long type) {
        return (A2Attribute) get(type, true);
    }

    public Object get(String name, boolean single) throws UnknownAttributeException {
        return get(AttributeFactory.getTypeByName(name), single);
    }

    public A2Attribute get(String name) throws UnknownAttributeException {
        return (A2Attribute) get(AttributeFactory.getTypeByName(name), true);
    }

    public Object getValue(long type) {
        A2Attribute attribute = get(type);
        if (attribute != null) {
            AttributeValue value = attribute.getValue();
            if (value != null) {
                return value.getValueObject();
            }
        }
        return null;
    }

    /**
     * Get all attributes of a certain type returned at an array
     * @param type The type of a2.packet.attribute to find
     * @return Returns an array of all attributes found of a certain type
     */
    public Object[] getArray(long type) {
        Long key = new Long(type);
        return toArray(attributeMap.get(key));
    }

    public String toString(boolean nonStandardAttrs, boolean unknownAttrs) {
        StringBuffer sb = new StringBuffer();
        Iterator<A2Attribute> i = attributeOrderList.iterator();
        while (i.hasNext()) {
            A2Attribute attr = (A2Attribute) i.next();
            if (!nonStandardAttrs && attr.attributeType > 256) continue;
            if (!unknownAttrs && attr instanceof UnknownAttribute) continue;
            sb.append(attr.toString()).append("\n");
        }
        return sb.toString();
    }

    public String toString() {
        return toString(true, true);
    }

    /**
     * Returns the a2.packet.attribute hash as a list
     * @return Returns a List of all attributes
     */
    public List<A2Attribute> getAttributeList() {
        return attributeOrderList;
    }

    /**
     * @return Returns the a2.packet.attribute map
     */
    public Map<Long, Object> getMap() {
        return attributeMap;
    }

    /**
     * Returns an a2.packet.attribute or list of attributes as an array
     * @param o The single a2.packet.attribute or LinkedList of attributes
     * @return Returns an array of RadiusAttributes
     */
    private Object[] toArray(Object o) {
        if (o == null) return null;

        Object ol[];

        if (o instanceof LinkedList<?>) {
            ol = ((LinkedList<?>) o).toArray();
        } else {
            ol = new Object[1];
            ol[0] = o;
        }
        return ol;
    }
}
