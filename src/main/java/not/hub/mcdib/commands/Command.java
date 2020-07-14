package not.hub.mcdib.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import not.hub.mcdib.util.Message;

import java.util.List;

public abstract class Command {

    // TODO: Command: Help Command (List of Commands & Arguments)
    // TODO: Command: Change Bot presence text & type
    // TODO: Command: purge chat history (argument: number of messages) or (argument: timestamp start deleterange)
    // TODO: Command: Enable Bridge Relay (mc, dc, both)
    // TODO: Command: Disable Bridge Relay (mc, dc, both)

    public final String name;
    public final int minArgs;
    public final int maxArgs;

    private final TextChannel channel;
    private final JDA jda;

    public Command(String name, int minArgs, int maxArgs, TextChannel channel, JDA jda) {
        this.name = name;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.channel = channel;
        this.jda = jda;
    }

    private void sendMessage(String message) {
        channel.sendMessage(new Message("mcdib", message).formatToDiscord()).queue();
    }

    /**
     * Extending commands do NOT want to override this but onCall(List<String> args) instead
     */
    public void call(List<String> args) {
        onCall(args);
    }

    /**
     * Extending commands want to override this to run logic on command call
     */
    private void onCall(List<String> args) {

    }

    @Override // generated
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return name.equals(command.name);
    }

    @Override // generated
    public int hashCode() {
        return name.hashCode();
    }

}
