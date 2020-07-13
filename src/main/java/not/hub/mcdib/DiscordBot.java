package not.hub.mcdib;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

public class DiscordBot extends ListenerAdapter {

    // m2dQueue & d2mQueue are used for inter thread communication.
    // they should be used in a way that the discord thread can be blocked
    // for a maximum of n ms (is there a discord connection timeout?)
    // but the mc thread will never get blocked by reading or writing the queues.
    // see BlockingQueue javadoc for read/write method explanation.
    private final BlockingQueue<Message> m2dQueue;
    private final BlockingQueue<Message> d2mQueue;

    private final Long bridgeChannelId;
    private final Set<Long> adminUserIds;
    private final Set<Long> adminRoleIds;

    private JDA jda;

    public DiscordBot(BlockingQueue<Message> m2dQueue, BlockingQueue<Message> d2mQueue, String token, Long bridgeChannelId, Set<Long> adminUserIds, Set<Long> adminRoleIds) {

        this.m2dQueue = m2dQueue;
        this.d2mQueue = d2mQueue;

        this.bridgeChannelId = bridgeChannelId;
        this.adminUserIds = adminUserIds;
        this.adminRoleIds = adminRoleIds;

        try {
            jda = JDABuilder
                    .createDefault(token)
                    .disableCache(
                            // https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/utils/cache/CacheFlag.html
                            // Cache is disabled by the following flags:
                            CacheFlag.ACTIVITY,                     // getActivities()
                            CacheFlag.CLIENT_STATUS,                // getOnlineStatus()
                            CacheFlag.EMOTE,                        // getEmoteCache()
                            CacheFlag.MEMBER_OVERRIDES,             // getMemberPermissionOverrides()
                            CacheFlag.VOICE_STATE                   // getVoiceState()
                            // Remove the related cache flag when using one of these methods in the bot!
                    )
                    .disableIntents(
                            // https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/GatewayIntent.html
                            // Gateway Events are disabled by the following flags:
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
                    )
                    .setActivity(Activity.watching("\uD83D\uDC53 Block Game Conversations"))
                    .addEventListeners(this)
                    .build();
            jda.awaitReady();

        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
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
                jda.getTextChannelById(bridgeChannelId).sendMessage(message.formatToDiscord()).queue();
            }
        }, 0, 100);

        // this returns null if the queue was empty:
        // String testfrommc = m2dQueue.poll();

        // this returns false if the element was not added (queue was full probably):
        // d2mQueue.offer("testfromdiscord");

    }

    public void shutdown() {
        jda.shutdown();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannel().getIdLong() == bridgeChannelId && !event.getAuthor().isBot() && event.getMessage().isFromType(ChannelType.TEXT)) {
            if (!d2mQueue.offer(new Message(event.getAuthor().getName(), event.getMessage().getContentRaw()))) {
                // TODO: warn in console
            }
        }
    }

}
