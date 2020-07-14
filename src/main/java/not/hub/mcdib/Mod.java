package not.hub.mcdib;

import not.hub.mcdib.util.Log;
import not.hub.mcdib.util.Message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class Mod extends JavaPlugin implements Listener {

    // TODO: Command System
    // TODO: Use for command auth:
    // Sets.newHashSet(getConfig().getLongList("discord-admin-user-ids"))
    // Sets.newHashSet(getConfig().getLongList("discord-admin-role-ids"))

    // TODO: Command: Help Command (List of Commands & Arguments)
    // TODO: Command: Change Bot presence text & type
    // TODO: Command: purge chat history (argument: number of messages) or (argument: timestamp start deleterange)
    // TODO: Command: Enable Bridge Relay (mc, dc, both)
    // TODO: Command: Disable Bridge Relay (mc, dc, both)

    // TODO: Automatic Slow Mode for bridge channel on spam (possible?)
    // TODO: Automatic message drop on spam (with prior announcement) (message rate threshold?)

    // TODO: add thread internal queue to be used buffer in case m2dQueue is full

    // TODO: write javadoc and replace scattered comments

    // m2dQueue & d2mQueue are used for inter thread communication.
    // they should be used in a way that the discord thread can be blocked
    // for a maximum of n ms (is there a discord connection timeout?)
    // but the mc thread will never get blocked by reading or writing the queues.
    // see BlockingQueue javadoc for read/write method explanation.
    private static final int QUEUE_CAPACITY = 100;
    private final BlockingQueue<Message> m2dQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
    private final BlockingQueue<Message> d2mQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

    private DiscordBot discordBot;

    @Override
    public void onEnable() {

        // load config, if false is returned stop further init
        if (!initConfig()) {
            return;
        }

        // run jda on second thread
        Thread botThread = new Thread(() -> discordBot = new DiscordBot(m2dQueue, d2mQueue,
                getConfig().getString("discord-bot-auth-token"),
                getConfig().getLong("discord-bridge-channel")));
        botThread.start();

        // TODO: replace timer with observer pattern
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (d2mQueue.peek() == null) {
                    return;
                }
                Message message = d2mQueue.poll();
                // TODO: use broadcast instead of players foreach? (does this spam console?)
                getServer().getOnlinePlayers().forEach(player -> player.sendMessage(message.formatToMc()));
            }
        }, 0, 100);

        // register mc chat listener
        getServer().getPluginManager().registerEvents(this, this);

    }

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        if (!m2dQueue.offer(new Message(event.getPlayer().getName(), event.getMessage()))) {
            Log.warn("Unable to insert Minecraft message into Discord send queue, message dropped...");
        }
    }

    @Override
    public void onDisable() {
        discordBot.shutdown();
        Log.info("Shutdown finished");
    }

    private boolean initConfig() {

        Log.info("Processing config");

        final String DEFAULT_TOKEN_VALUE = "AAAAAAAAAAAAAAAAAAAAAAAA.AAAAAA.AAAAAAAAAAAAAAAAAAAAAAAAAAA";
        final Long DEFAULT_ID_VALUE = 111111111111111111L;

        getConfig().addDefault("discord-bot-auth-token", DEFAULT_TOKEN_VALUE);
        getConfig().addDefault("discord-bridge-channel", DEFAULT_ID_VALUE);
        getConfig().addDefault("discord-admin-user-ids", Arrays.asList(DEFAULT_ID_VALUE, DEFAULT_ID_VALUE));
        getConfig().addDefault("discord-admin-role-ids", Arrays.asList(DEFAULT_ID_VALUE, DEFAULT_ID_VALUE));
        getConfig().options().copyDefaults(true);
        saveConfig();

        // TODO: regex check for token & ids

        String token = getConfig().getString("discord-bot-auth-token");
        if (token == null || token.equals(DEFAULT_TOKEN_VALUE)) {
            Log.warn("Please supply a bot token! halting mcdib initialization...");
            return false;
        }

        Long channel = getConfig().getLong("discord-bridge-channel");
        if (channel.equals(DEFAULT_ID_VALUE)) {
            Log.warn("Please supply a bridge channel id! halting mcdib initialization...");
            return false;
        }

        return true;

    }

}
