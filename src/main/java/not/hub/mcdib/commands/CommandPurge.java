package not.hub.mcdib.commands;

import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class CommandPurge extends Command {

    // TODO: Command: purge chat history (argument: number of messages) or (argument: timestamp start deleterange)

    public CommandPurge(TextChannel channel) {
        super("purge", 1, 1, channel);
    }

    @Override
    public void run(List<String> args) {
        sendMessageToDiscord("Hi there, you ran the purge command: " + args.toString());
    }

}
