package not.hub.mcdib.messages;

public class ChatMessage {

    // TODO: split in abstract and message types

    private final String sender;
    private final String message;
    private final boolean rawMessage;

    public ChatMessage(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.rawMessage = false;
    }

    public ChatMessage(String message) {
        this.sender = "";
        this.message = message;
        this.rawMessage = true;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRawMessage() {
        return rawMessage;
    }

}
