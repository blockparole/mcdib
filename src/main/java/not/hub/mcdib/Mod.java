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

    // TODO: write javadoc and replace scattered comments

    // m2dQueue & d2mQueue are used for inter thread communication.
    // they should be used in a way so the discord thread can be blocked
    // for a maximum of n ms (is there a discord connection timeout?)
    // but the mc thread will never get blocked by reading or writing the queues.
    // ideally, nothing will ever get blocked. thread blocking can result from: TODO
    // see BlockingQueue javadoc for read/write method explanation.
    // !m2dQueue & d2mQueue are the only gates of communication to use between the threads!
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

        // starting bot on second thread
        // everything thats not related to:
        // delivering and receiving messages from the queues,
        // delivering discord messages to mc chat and plugin init should run on this thread.
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
                sendMessageToMinecraft(message);
            }
        }, 0, 100);

        // register mc chat listener
        getServer().getPluginManager().registerEvents(this, this);

    }

    // send mc chat to discord
    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        if (!m2dQueue.offer(new Message(event.getPlayer().getName(), event.getMessage()))) {
            Log.warn("Unable to insert Minecraft message into Discord send queue, message dropped...");
        }
    }

    // This wont fire if the server is not stopped normally (process killed etc.)
    @Override
    public void onDisable() {
        discordBot.shutdown();
        Log.info("Shutdown finished");
    }

    private void sendMessageToMinecraft(Message message) {
        // TODO: use broadcast instead of players foreach? (does this spam console?)
        getServer().getOnlinePlayers().forEach(player -> player.sendMessage(message.formatToMc()));
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
