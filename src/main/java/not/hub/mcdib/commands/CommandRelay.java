package not.hub.mcdib.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import not.hub.mcdib.DiscordBot;

import java.util.List;

public class CommandRelay extends Command {

    // TODO: Command: Enable Bridge Relay
    // TODO: Command: Disable Bridge Relay
    // TODO: indicate status with presence idle true false

    public CommandRelay(TextChannel channel, DiscordBot discordBot) {
        super("relay", 1, 1, channel, discordBot);
    }

    @Override
    public void run(List<String> args) {
        sendMessageToDiscord("Hi there, you ran the relay command: " + args.toString());
    }

}
