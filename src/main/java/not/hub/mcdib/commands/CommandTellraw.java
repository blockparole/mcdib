package not.hub.mcdib.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import not.hub.mcdib.DiscordBot;

import java.util.List;

public class CommandTellraw extends Command {

    // TODO: Command: tellraw style message sender

    public CommandTellraw(DiscordBot discordBot) {
        super("tellraw", "send raw chat messages to minecraft", 1, 1, discordBot);
    }

    @Override
    public void run(List<String> args) {
        sendToDiscord("Hi there, you ran the tellraw command: " + args.toString() + " This Command is not implemented yet. :(");
    }

}
