package not.hub.mcdib.utils;

import not.hub.mcdib.messages.ChatMessage;

public class ChatSanitizer {

    public static String formatToMc(ChatMessage chatMessage) {
        String out = "<DC:" + filterToMc(chatMessage.getSender()) + "> " + filterToMc(chatMessage.getMessage());
        return out.substring(0, Math.min(out.length(), 256));
    }

    public static String formatToDiscord(ChatMessage chatMessage) {
        String out = filterToDiscord(chatMessage.getSender()) + "```" + "\n" + filterToDiscord(chatMessage.getMessage()); // System.lineSeparator() causes discord to show 2 linebreaks on mobile
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

    // should tellraw actually be filtered?
    public static String filterTellraw(String raw) {
        // TODO: sane regex chat filter
        raw = raw.replaceAll("/", " ");
        return raw;
    }

}
