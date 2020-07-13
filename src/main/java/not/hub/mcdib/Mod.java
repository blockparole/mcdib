package not.hub.mcdib;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class Mod extends JavaPlugin {

    private static final String DEFAULT_TOKEN_VALUE = "bottokengoeshere";

    // m2dQueue & d2mQueue are used for inter thread communication.
    // they should be used in a way that the discord thread can be blocked
    // for a maximum of n ms (is there a discord connection timeout?)
    // but the mc thread will never get blocked by reading or writing the queues.
    // see BlockingQueue javadoc for read/write method explanation.
    final BlockingQueue<String> m2dQueue = new LinkedBlockingQueue<>();
    final BlockingQueue<String> d2mQueue = new LinkedBlockingQueue<>();

    DiscordBot discordBot;

    @Override
    public void onEnable() {

        // load config, if false is returned, halt enabling mcdib
        if (!initConfig()) {
            return;
        }

        // run discord4j on second thread
        Thread botThread = new Thread(() -> {
            discordBot = new DiscordBot(getConfig().getString("discord-bot-token"), m2dQueue, d2mQueue);
        });
        botThread.start();

        // TODO: pipe mc chat to bot thread by filling m2dqueue via chat listener
        // this returns false if the element was not added (queue was full probably):
        // m2dQueue.offer("testfrommc");

        // TODO: periodically (1000ms?) pipe discord chat from bot thread into mc chat via d2mqueue
        // this returns null if the queue was empty:
        // String testfromdiscord = d2mQueue.poll();

    }

    private boolean initConfig() {

        getConfig().addDefault("discord-bot-token", DEFAULT_TOKEN_VALUE);
        getConfig().options().copyDefaults(true);
        saveConfig();

        String token = getConfig().getString("discord-bot-token");
        if (token != null && token.equals(DEFAULT_TOKEN_VALUE)) {
            getLogger().warning("Please supply a bot token! mcdib shutting down...");
            return false;
        }

        return true;

    }

    @Override
    public void onDisable() {
        discordBot.shutdown();
    }

}
