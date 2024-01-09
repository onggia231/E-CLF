package a2.packet.attribute.value;

import a2.log.RadiusLog;

import java.io.Serializable;

/**
 * The "Named Value" a2.packet.attribute value (Integer enumerated attributes)
 *
 * @author David Bird
 */
public class NamedValue extends IntegerValue {
    private static final long serialVersionUID = 0L;

    public interface NamedValueMap {
        public String getNamedValue(Long l);

        public Long getNamedValue(String s);

        public Long[] getKnownValues();
    }

    private transient NamedValueMap valueMap = null;

    public NamedValue(NamedValueMap map) {
        valueMap = map;
    }

    public NamedValue(NamedValueMap map, String s) {
        valueMap = map;
        setValue(s);
    }

    public NamedValue(NamedValueMap map, Long l) {
        valueMap = map;
        setValue(l);
    }

    public NamedValue(NamedValueMap map, Integer i) {
        valueMap = map;
        setValue(i);
    }

    public NamedValue(Integer i) {
        setValue(i);
    }

    public void setValue(String s) {
        if (valueMap == null) return;
        Long i = valueMap.getNamedValue(s);
        if (i != null) {
            this.integerValue = i;
        } else {
            RadiusLog.error("Invalid NamedValue string value: " + s);
        }
    }

    public void setValue(Number l) {
        this.integerValue = new Long(l.longValue());
    }

    public void setValueObject(Serializable o) {
        if (o instanceof Number) {
            setValue((Number) o);
        } else {
            setValue(o.toString());
        }
    }

    public String getValueString() {
        if (valueMap == null) return null;
        return valueMap.getNamedValue(integerValue);
    }

    public NamedValueMap getMap() {
        return valueMap;
    }

    public String toString() {
        if (valueMap != null) {
            String s = valueMap.getNamedValue(integerValue);
            if (s != null) return s;
        }
        return "Unknown-" + integerValue;
    }
}
