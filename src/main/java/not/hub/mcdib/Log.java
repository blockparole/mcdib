package not.hub.mcdib;

import static org.bukkit.Bukkit.getLogger;

public class Log {

    private static final java.util.logging.Logger LOG = getLogger();

    public static void info(String message) {
        LOG.info(format(message));
    }

    public static void warn(String message) {
        LOG.warning(format(message));
    }

    private static String format(String message) {
        return "[MCDIB] " + message;
    }

}
