package not.hub.mcdib.messages;

public class ChatMessage extends Message {

    private final String sender;

    public ChatMessage(String sender, String message) {
        super(message);
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

}
