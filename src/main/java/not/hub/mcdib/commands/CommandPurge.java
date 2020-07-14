package not.hub.mcdib.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import not.hub.mcdib.DiscordBot;

import java.util.List;

public class CommandPurge extends Command {

    // TODO: Command: purge chat history (argument: number of messages) or (argument: timestamp start deleterange)

    public CommandPurge(DiscordBot discordBot) {
        super("purge", "purge messages in bridge channel", 1, 1, discordBot);
    }

    @Override
    public void run(List<String> args) {
        sendToDiscord("Hi there, you ran the purge command: " + args.toString() + " This Command is not implemented yet. :(");
    }

}
