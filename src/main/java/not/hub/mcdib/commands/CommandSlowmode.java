package not.hub.mcdib.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import not.hub.mcdib.DiscordBot;

import java.util.List;

public class CommandSlowmode extends Command {

    // TODO: Command: slowmode command to set enable / disable auto slow mode

    public CommandSlowmode(DiscordBot discordBot) {
        super("slowmode", "turn automatic slowmode on / off", 1, 1, discordBot);
    }

    @Override
    public void run(List<String> args) {
        sendToDiscord("Hi there, you ran the slowmode command: " + args.toString() + " This Command is not implemented yet. :(");
    }

}
