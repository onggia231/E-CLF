package a2.packet.attribute;

/**
 * @author David Bird
 */
public class SubAttribute extends VSAttribute
{
	private static final long serialVersionUID = 1L;
	private Class<?> parentClass;
	private int flags;
	
	public void setup() 
	{
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public Class<?> getParentClass() {
		return parentClass;
	}

	public void setParentClass(Class<?> parentClass) {
		this.parentClass = parentClass;
	}
	
}
