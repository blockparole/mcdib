package not.hub.mcdib.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import not.hub.mcdib.util.Message;

import java.util.List;

public abstract class Command {

    public final String name;

    // TODO: replace min and max ints with int ranges so we can have commands with dynamic arg count
    public final int minArgs;
    public final int maxArgs;

    private final TextChannel channel;

    public Command(String name, int minArgs, int maxArgs, TextChannel channel) {
        this.name = name;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.channel = channel;
    }

    private void sendMessage(String message) {
        channel.sendMessage(new Message("mcdib", message).formatToDiscord()).queue();
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
        channel.sendMessage(new Message("mcdib", message).formatToDiscord()).queue();
    }

}
