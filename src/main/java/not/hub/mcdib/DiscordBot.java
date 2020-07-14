package not.hub.mcdib;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import not.hub.mcdib.util.Log;
import not.hub.mcdib.util.Message;

import javax.security.auth.login.LoginException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

public class DiscordBot extends ListenerAdapter {

    // TODO: Command System
    // TODO: Use for command auth:
    // Sets.newHashSet(getConfig().getLongList("discord-admin-user-ids"))
    // Sets.newHashSet(getConfig().getLongList("discord-admin-role-ids"))

    // TODO: Automatic Slow Mode for bridge channel on spam (possible?)
    // TODO: Automatic message drop on spam (with prior announcement) (message rate threshold?)

    private final BlockingQueue<Message> d2mQueue;
    private final Long bridgeChannelId;

    private JDA jda;

    public DiscordBot(BlockingQueue<Message> m2dQueue, BlockingQueue<Message> d2mQueue, String token, Long bridgeChannelId) {

        this.d2mQueue = d2mQueue;
        this.bridgeChannelId = bridgeChannelId;

        try {
            jda = JDABuilder
                    .createDefault(token)
                    .disableCache(
                            // https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/utils/cache/CacheFlag.html
                            // Caches are disabled by the following flags:
                            CacheFlag.ACTIVITY,                     // getActivities()
                            CacheFlag.CLIENT_STATUS,                // getOnlineStatus()
                            CacheFlag.EMOTE,                        // getEmoteCache()
                            CacheFlag.MEMBER_OVERRIDES,             // getMemberPermissionOverrides()
                            CacheFlag.VOICE_STATE                   // getVoiceState()
                            // !Remove the related cache flag when using one of these methods in the bot!
                    ).disableIntents(
                            // https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/GatewayIntent.html
                            // Gateway Intent Events are disabled by the following flags:
                            GatewayIntent.GUILD_MEMBERS,            // This is a privileged gateway intent that is used to update user information and join/leaves (including kicks). This is required to cache all members of a guild (including chunking)
                            GatewayIntent.GUILD_BANS,               // This will only track guild bans and unbans
                            GatewayIntent.GUILD_EMOJIS,             // This will only track guild emote create/modify/delete. Most bots don't need this since they just use the emote id anyway.
                            GatewayIntent.GUILD_INVITES,            // This will only track invite create/delete. Most bots don't make use of invites since they are added through OAuth2 authorization by administrators.
                            GatewayIntent.GUILD_VOICE_STATES,       // Required to properly get information of members in voice channels and cache them. You cannot connect to a voice channel without this intent.
                            GatewayIntent.GUILD_PRESENCES,          // This is a privileged gateway intent this is only used to track activity and online-status of a user.
                            // GatewayIntent.GUILD_MESSAGES,           // This is used to receive incoming messages in guilds (servers), most bots will need this for commands.
                            GatewayIntent.GUILD_MESSAGE_REACTIONS,  // This is used to track reactions on messages in guilds (servers). Can be useful to make a paginated embed or reaction role management.
                            GatewayIntent.GUILD_MESSAGE_TYPING,     // This is used to track when a user starts typing in guilds (servers). Almost no bot will have a use for this.
                            GatewayIntent.DIRECT_MESSAGES,          // This is used to receive incoming messages in private channels (DMs). You can still send private messages without this intent.
                            GatewayIntent.DIRECT_MESSAGE_REACTIONS, // This is used to track reactions on messages in private channels (DMs).
                            GatewayIntent.DIRECT_MESSAGE_TYPING     // This is used to track when a user starts typing in private channels (DMs). Almost no bot will have a use for this.
                            // If an intent is not specifically mentioned to be privileged,
                            // it is not required to be on the whitelist to use if (and its related events).
                            // To get whitelisted you either need to contact discord support (for bots in more than 100 guilds)
                            // or enable it in the developer dashboard of your application.
                            // You must use ChunkingFilter.NONE if GUILD_MEMBERS is disabled.
                            // To enable chunking the discord api requires the privileged GUILD_MEMBERS intent.
                            // !Remove the related cache flag when using one of these events in the bot!
                    ).setActivity(Activity.watching("\uD83D\uDC53 Block Game Conversations"))
                    .addEventListeners(this)
                    .build();
            jda.awaitReady();

        } catch (LoginException e) {
            Log.warn("Discord login failed! halting mcdib initialization...");
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.warn("JDA initialization failed! halting mcdib initialization...");
            return;
        }

        TextChannel channel = jda.getTextChannelById(bridgeChannelId);

        if (channel == null) {
            Log.warn("Unable to find bridge channel by id (" + bridgeChannelId + ")! halting mcdib initialization...");
            return;
        }

        // TODO: replace timer with observer pattern
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (m2dQueue.peek() == null) {
                    return;
                }
                Message message = m2dQueue.poll();
                sendMessageToDiscord(message);
            }
        }, 0, 100);

        Log.info("Initialization finished! Bridge channel is #" + channel.getName() + " on " + channel.getGuild().getName());

    }

    private void sendMessageToDiscord(Message message) {
        TextChannel channel = jda.getTextChannelById(bridgeChannelId);
        if (channel == null) {
            Log.warn("Unable to find bridge channel by id (" + bridgeChannelId + ")!");
            return;
        }
        channel.sendMessage(message.formatToDiscord()).queue();
    }

    // This wont fire if the server is not stopped normally (process killed etc.)
    public void shutdown() {
        Log.info("Shutting down JDA");
        jda.shutdown();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannel().getIdLong() == bridgeChannelId && !event.getAuthor().isBot() && event.getMessage().isFromType(ChannelType.TEXT)) {
            if (!d2mQueue.offer(new Message(event.getAuthor().getName(), event.getMessage().getContentRaw()))) {
                Log.warn("Unable to insert Discord message into Minecraft send queue, message dropped...");
            }
        }
    }

}
