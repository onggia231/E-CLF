package a2.server;


/**
 * The JRadiusEvent is the base class for server events and JRadiusRequests.
 *
 * @author Gert Jan Verhoog
 * @author David Bird
 */
public abstract class JRadiusEvent {
    private Listener listener;
    private Object sender;

    /**
     * @return Returns the type of the JRadiusRequest
     */
    public abstract int getType();

    /**
     * @return Returns the type of the JRadiusRequest
     */
    public abstract String getTypeString();


    public Object getSender() {
        return sender;
    }

    public void setSender(Object sender) {
        this.sender = sender;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
