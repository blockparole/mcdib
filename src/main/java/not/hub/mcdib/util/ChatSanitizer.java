package not.hub.mcdib.util;

public class ChatSanitizer {

    public static String formatToMc(RelayMessage relayMessage) {
        String out = "<DC:" + filterToMc(relayMessage.sender) + "> " + filterToMc(relayMessage.message);
        return out.substring(0, Math.min(out.length(), 256));
    }

    public static String formatToDiscord(RelayMessage relayMessage) {
        String out = filterToDiscord(relayMessage.sender) + "```" + "\n" + filterToDiscord(relayMessage.message); // System.lineSeparator() causes discord to show 2 linebreaks on mobile
        return out.substring(0, Math.min(out.length(), 2000 - 3)) + "```"; // in case someone manages to send 2k char messages
    }

    public static String filterToMc(String raw) {
        raw = filterPre(raw);
        // TODO: sane regex chat filter
        // ascii range 0x00 - 0x80 or bad idea?
        return raw.replaceAll("§", "");
    }

    public static String filterToDiscord(String raw) {
        raw = filterPre(raw);
        // TODO: sane regex chat filter
        return raw;
    }

    public static String filterPre(String raw) {
        // TODO: sane regex chat filter
        raw = raw.replaceAll("\\$", " "); // replace linebreaks
        return raw;
    }

}
