package not.hub.mcdib.messages;

public class InfoMessage extends ChatMessage {

    private static final String UNICODE_ROBOT = "\ud83e\udd16";

    public InfoMessage(String message) {
        super(UNICODE_ROBOT, message);
    }

}
