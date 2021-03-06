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
import not.hub.mcdib.messages.ChatMessage;
import not.hub.mcdib.messages.ConfigMessage;
import not.hub.mcdib.messages.Message;
import not.hub.mcdib.messages.RawMessage;
import not.hub.mcdib.utils.ChatSanitizer;
import not.hub.mcdib.utils.Log;
import not.hub.mcdib.utils.PresenceGenerator;
import not.hub.mcdib.utils.Snowflake;

import javax.security.auth.login.LoginException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class DiscordBot extends ListenerAdapter {

    // TODO: Command System
    // TODO: Use for command auth:
    // Sets.newHashSet(getConfig().getLongList("discord-admin-user-ids"))
    // Sets.newHashSet(getConfig().getLongList("discord-admin-role-ids"))

    // TODO: Automatic Slow Mode for bridge channel on spam (possible?)
    // TODO: Automatic message drop on spam (with prior announcement) (message rate threshold?)

    private final BlockingQueue<Message> d2mQueue;
    private final Snowflake sfChannel;
    private final Set<Snowflake> sfAdmins;

    private Boolean m2dEnabled;
    private Boolean d2mEnabled;

    private String commandPrefix;

    private AntiFlood antiFlood;
    private CommandProcessor commandProcessor;

    private JDA jda;

    public DiscordBot(BlockingQueue<Message> m2dQueue, BlockingQueue<Message> d2mQueue, String token, Snowflake sfChannel, Set<Snowflake> sfAdmins, String commandPrefix) {

        this.d2mQueue = d2mQueue;
        this.sfChannel = sfChannel;
        this.sfAdmins = sfAdmins;
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

        TextChannel bridgeChannel = jda.getTextChannelById(sfChannel.id);

        if (bridgeChannel == null) {
            Log.warn("Unable to find bridge channel by id (" + sfChannel + ")! halting mcdib initialization...");
            return;
        }

        antiFlood = new AntiFlood(true, true, 15, 30, this);
        commandProcessor = new CommandProcessor(bridgeChannel, this);

        PresenceGenerator.updatePresence(this);

        // TODO: replace timer with observer pattern
        // receive minecraft chat from mc thread
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (m2dQueue.peek() == null) {
                    return;
                }

                Message message = m2dQueue.poll();

                // We only receive ChatMessage on this channel, everything else will be ignored
                if (!(message instanceof ChatMessage)) {
                    return;
                }

                antiFlood.icrementM2dCounter();
                if (m2dEnabled && !antiFlood.shouldDropM2dChatMessages()) {
                    sendMessageToDiscord((ChatMessage) message);
                }

            }
        }, 0, 100);

        Log.info("Initialization finished! Bridge channel is #" + bridgeChannel.getName() + " on " + bridgeChannel.getGuild().getName());

    }

    public void sendMessageToDiscord(ChatMessage chatMessage) {
        TextChannel channel = jda.getTextChannelById(sfChannel.id);
        if (channel == null) {
            Log.warn("Unable to find bridge channel by id (" + sfChannel.toString() + ")!");
            return;
        }
        channel.sendMessage(ChatSanitizer.formatToDiscord(chatMessage)).queue();
    }

    public void sendMessageToMinecraft(ChatMessage message) {
        if (!d2mQueue.offer(message)) {
            Log.warn("Unable to insert Discord message into Minecraft send queue, message dropped... Something seems wrong, check your logs!");
        }
    }

    public void sendMessageToMinecraft(RawMessage message) {
        if (!d2mQueue.offer(message)) {
            Log.warn("Unable to insert raw message into Minecraft send queue, message dropped... Something seems wrong, check your logs!");
        }
    }

    public void sendConfigMessage(ConfigMessage message) {
        if (!d2mQueue.offer(message)) {
            Log.warn("Unable to insert config message into Minecraft send queue, message dropped... Something seems wrong, check your logs!");
        }
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
                event.getChannel().getIdLong() != sfChannel.id
                        || event.getAuthor().isBot()
                        || !event.getMessage().isFromType(ChannelType.TEXT)
        ) {
            return;
        }

        // check for command
        // check admin permissions by long id
        if (sfAdmins.stream().map(Snowflake::toLong).collect(Collectors.toSet()).contains(event.getAuthor().getIdLong())) {
            String raw = event.getMessage().getContentRaw();
            // message cant be empty, must have prefix as first char and length must be > 1 (including prefix)
            if (!raw.isEmpty() && raw.substring(0, 1).equals(commandPrefix) && raw.length() > 1) {
                List<String> messageElements = Arrays.asList(raw.substring(1).split(" "));
                if (messageElements.size() == 1) {
                    processCommand(messageElements.get(0), Collections.emptyList(), event.getAuthor());
                    return;
                } else if (messageElements.size() > 1) {
                    processCommand(messageElements.get(0), messageElements.subList(1, messageElements.size()), event.getAuthor());
                    return;
                }
            }
        }

        // relay discord chat to mc
        antiFlood.icrementD2mCounter();
        if (d2mEnabled && !antiFlood.shouldDropD2mChatMessages()) {
            String message = event.getMessage().getContentRaw();
            if (ChatSanitizer.filterToMc(message).isEmpty()) return;
            sendMessageToMinecraft(new ChatMessage(event.getAuthor().getName(), message));
        }

    }

    private void processCommand(String command, List<String> args, User author) {
        Log.info(author.getName() + " (" + author.getIdLong() + ") issued command: " + command + " with arguments: " + args.toString());
        commandProcessor.processCommand(command, args);
    }

    public JDA getJda() {
        return jda;
    }

    public AntiFlood getAntiFlood() {
        return antiFlood;
    }

    public CommandProcessor getCommandProcessor() {
        return commandProcessor;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public Snowflake getSfChannel() {
        return sfChannel;
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
