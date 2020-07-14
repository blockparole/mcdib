package not.hub.mcdib.util;

public class Message {

    final String sender;
    final String message;

    public Message(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String formatToMc() {
        String out = "<DC:" + filter(sender) + "> " + filter(message);
        return out.substring(0, Math.min(out.length(), 256));
    }

    public String formatToDiscord() {
        String out = sender + "```" + System.lineSeparator() + filter(message);
        return out.substring(0, Math.min(out.length(), 2000 - 3)) + "```"; // in case someone manages to send 2k char messages
    }

    private String filter(String raw) {
        // TODO: sane regex chat filter
        return raw.replaceAll("§", "");
    }

}
