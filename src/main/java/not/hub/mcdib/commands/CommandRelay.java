package not.hub.mcdib.commands;

import not.hub.mcdib.DiscordBot;

import java.util.List;

public class CommandRelay extends Command {

    // TODO: Command: Enable Bridge Relay
    // TODO: Command: Disable Bridge Relay
    // TODO: indicate status with presence idle true false

    public CommandRelay(DiscordBot discordBot) {
        super("relay", "turn chat relay on / off", 1, 1, discordBot);
    }

    @Override
    public void run(List<String> args) {
        sendToDiscord("Hi there, you ran the relay command: " + args.toString() + " This Command is not implemented yet. :(");
    }

}
