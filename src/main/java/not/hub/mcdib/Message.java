package not.hub.mcdib;

public class Message {

    final String sender;
    final String message;

    public Message(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String formatToMc() {
        String out = format("DC:" + sender, message);
        return out.substring(0, Math.min(out.length(), 256));
    }

    public String formatToDiscord() {
        String out = format(sender, message);
        return out.substring(0, Math.min(out.length(), 2000)); // in case someone manages to send 2k char messages
    }

    private String format(String sender, String message) {
        return "<" + filter(sender) + ">" + " " + filter(message);
    }

    private String filter(String raw) {
        // TODO: regex chat filter
        return raw;
    }

}
