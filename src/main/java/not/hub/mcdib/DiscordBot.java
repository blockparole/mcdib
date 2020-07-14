package not.hub.mcdib;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import not.hub.mcdib.message.ChatMessage;
import not.hub.mcdib.util.ChatSanitizer;
import not.hub.mcdib.util.FloodCounter;
import not.hub.mcdib.util.Log;
import not.hub.mcdib.util.PresenceGenerator;

import javax.security.auth.login.LoginException;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class DiscordBot extends ListenerAdapter {

    // TODO: Command System
    // TODO: Use for command auth:
    // Sets.newHashSet(getConfig().getLongList("discord-admin-user-ids"))
    // Sets.newHashSet(getConfig().getLongList("discord-admin-role-ids"))

    // TODO: Automatic Slow Mode for bridge channel on spam (possible?)
    // TODO: Automatic message drop on spam (with prior announcement) (message rate threshold?)

    private final BlockingQueue<ChatMessage> d2mQueue;
    private final Long bridgeChannelId;
    private final List<Long> adminIds;
    private final char commandPrefix;

    private Boolean m2dEnabled;
    private Boolean d2mEnabled;

    private FloodCounter floodCounter;

    private CommandProcessor commandProcessor;

    private JDA jda;

    public DiscordBot(BlockingQueue<ChatMessage> m2dQueue, BlockingQueue<ChatMessage> d2mQueue, String token, long bridgeChannelId, List<Long> adminIds, char commandPrefix) {

        this.d2mQueue = d2mQueue;
        this.bridgeChannelId = bridgeChannelId;
        this.adminIds = adminIds;
        this.commandPrefix = commandPrefix;

        this.m2dEnabled = true;
        this.d2mEnabled = true;

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
                    ).addEventListeners(this)
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

        TextChannel bridgeChannel = jda.getTextChannelById(bridgeChannelId);

        if (bridgeChannel == null) {
            Log.warn("Unable to find bridge channel by id (" + bridgeChannelId + ")! halting mcdib initialization...");
            return;
        }

        floodCounter = new FloodCounter(false);
        commandProcessor = new CommandProcessor(bridgeChannel, this);

        PresenceGenerator.updatePresence(jda.getPresence(), d2mEnabled, m2dEnabled);

        // TODO: replace timer with observer pattern
        // receive minecraft chat from mc thread
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (m2dQueue.peek() == null) {
                    return;
                }
                floodCounter.icrementM2dCounter();
                ChatMessage chatMessage = m2dQueue.poll();
                if (m2dEnabled && !floodCounter.isM2dFlood()) {
                    sendMessageToDiscord(chatMessage);
                }
            }
        }, 0, 100);

        Log.info("Initialization finished! Bridge channel is #" + bridgeChannel.getName() + " on " + bridgeChannel.getGuild().getName());

    }

    public void sendMessageToDiscord(ChatMessage chatMessage) {
        TextChannel channel = jda.getTextChannelById(bridgeChannelId);
        if (channel == null) {
            Log.warn("Unable to find bridge channel by id (" + bridgeChannelId + ")!");
            return;
        }
        channel.sendMessage(ChatSanitizer.formatToDiscord(chatMessage)).queue();
    }

    // This wont fire if the server is not stopped normally (process killed etc.)
    public void shutdown() {
        Log.info("Shutting down JDA");
        jda.shutdown();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        // we dont want this kind of messages
        if (
                event.getChannel().getIdLong() != bridgeChannelId
                        || event.getAuthor().isBot()
                        || !event.getMessage().isFromType(ChannelType.TEXT)
        ) {
            return;
        }

        // check for command
        // check admin permissions by long id
        if (adminIds.contains(event.getAuthor().getIdLong())) {
            String raw = event.getMessage().getContentRaw();
            // message cant be empty, must have prefix as first char and length must be > 1 (including prefix)
            if (!raw.isEmpty() && raw.charAt(0) == commandPrefix && raw.length() > 1) {
                List<String> messageElements = Arrays.asList(raw.substring(1).split(" "));
                if (messageElements.size() == 1) {
                    runCommand(messageElements.get(0), Collections.emptyList(), event.getAuthor());
                    return;
                } else if (messageElements.size() > 1) {
                    runCommand(messageElements.get(0), messageElements.subList(1, messageElements.size()), event.getAuthor());
                    return;
                }
            }
        }

        // relay discord chat to mc
        floodCounter.icrementD2mCounter();
        if (d2mEnabled && !floodCounter.isD2mFlood()) {
            String message = event.getMessage().getContentRaw();
            if (ChatSanitizer.filterToMc(message).isEmpty()) return;
            if (!d2mQueue.offer(new ChatMessage(event.getAuthor().getName(), message))) {
                Log.warn("Unable to insert Discord message into Minecraft send queue, message dropped... Something seems wrong, check your logs!");
            }
        }

    }

    private void runCommand(String command, List<String> args, User author) {
        Log.info(author.getName() + " (" + author.getIdLong() + ") issued command: " + command + " with arguments: " + args.toString());
        commandProcessor.processCommand(command, args);
    }

    public JDA getJda() {
        return jda;
    }

    public FloodCounter getFloodCounter() {
        return floodCounter;
    }

    public CommandProcessor getCommandProcessor() {
        return commandProcessor;
    }

    public char getCommandPrefix() {
        return commandPrefix;
    }

    public Long getBridgeChannelId() {
        return bridgeChannelId;
    }

    public BlockingQueue<ChatMessage> getD2mQueue() {
        return d2mQueue;
    }

    public boolean getM2dEnabled() {
        return m2dEnabled;
    }

    public void setM2dEnabled(Boolean m2dEnabled) {
        this.m2dEnabled = m2dEnabled;
    }

    public boolean getD2mEnabled() {
        return d2mEnabled;
    }

    public void setD2mEnabled(Boolean d2mEnabled) {
        this.d2mEnabled = d2mEnabled;
    }

}
