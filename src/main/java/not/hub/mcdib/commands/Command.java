package not.hub.mcdib.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import not.hub.mcdib.DiscordBot;
import not.hub.mcdib.util.Message;

import java.util.List;

public abstract class Command {

    public final String name;

    // TODO: replace min and max ints with int ranges so we can have commands with dynamic arg count
    public final int minArgs;
    public final int maxArgs;

    private final TextChannel channel;
    private final DiscordBot bot;

    public Command(String name, int minArgs, int maxArgs, TextChannel channel, DiscordBot bot) {
        this.name = name;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.channel = channel;
        this.bot = bot;
    }

    /**
     * Extending commands want to override this to run logic on command call
     */
    public void run(List<String> args) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return name.equals(command.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public void sendMessageToDiscord(String message) {
        bot.sendMessageToDiscord(new Message("\uD83E\uDD16", message));
    }

    DiscordBot getBot() {
        return bot;
    }

    TextChannel getChannel() {
        return channel;
    }

    public String getName() {
        return name;
    }

    public int getMinArgs() {
        return minArgs;
    }

    public int getMaxArgs() {
        return maxArgs;
    }

}
