package not.hub.mcdib.commands;

import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class CommandRelay extends Command {

    // TODO: Command: Enable Bridge Relay (mc, dc, both)
    // TODO: Command: Disable Bridge Relay (mc, dc, both)

    public CommandRelay(TextChannel channel) {
        super("relay", 1, 1, channel);
    }

    @Override
    public void run(List<String> args) {
        sendMessageToDiscord("Hi there, you ran the relay command: " + args.toString());
    }

}
