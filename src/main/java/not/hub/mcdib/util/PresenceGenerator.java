package not.hub.mcdib.util;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;

import java.util.AbstractMap;

public class PresenceGenerator {

    private static AbstractMap.SimpleEntry<OnlineStatus, Activity> generateActivity(boolean d2m, boolean m2d) {
        if (d2m || m2d) {
            return new AbstractMap.SimpleEntry<>(OnlineStatus.ONLINE, Activity.listening("DC " + (m2d ? "<" : "|") + "-" + (d2m ? ">" : "|") + " MC"));
        } else {
            return new AbstractMap.SimpleEntry<>(OnlineStatus.IDLE, Activity.listening("counting electric sheep \uD83D\uDE34"));
        }
    }

    public static void updatePresence(Presence sessionPresence, boolean d2m, boolean m2d) {
        AbstractMap.SimpleEntry<OnlineStatus, Activity> presence = generateActivity(d2m, m2d);
        sessionPresence.setPresence(presence.getKey(), presence.getValue());
    }

}
