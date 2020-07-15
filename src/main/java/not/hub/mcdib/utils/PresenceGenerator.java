package not.hub.mcdib.utils;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import not.hub.mcdib.DiscordBot;

import java.util.AbstractMap;

public class PresenceGenerator {

    private static AbstractMap.SimpleEntry<OnlineStatus, Activity> generateActivity(boolean d2m, boolean m2d) {
        if (d2m || m2d) {
            return new AbstractMap.SimpleEntry<>(OnlineStatus.ONLINE, Activity.listening("DC " + (m2d ? "<" : "|") + "-" + (d2m ? ">" : "|") + " MC"));
        } else {
            return new AbstractMap.SimpleEntry<>(OnlineStatus.IDLE, Activity.listening("counting electric sheep \uD83D\uDE34"));
        }
    }

    public static void updatePresence(DiscordBot bot) {
        AbstractMap.SimpleEntry<OnlineStatus, Activity> presence = generateActivity(
                bot.getD2mEnabled() && !(bot.getAntiFlood().shouldDropD2mChatMessages()),
                bot.getM2dEnabled() && !(bot.getAntiFlood().shouldDropM2dChatMessages()));
        bot.getJda().getPresence().setPresence(presence.getKey(), presence.getValue());
    }

}
