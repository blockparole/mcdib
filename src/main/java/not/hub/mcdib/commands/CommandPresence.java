package not.hub.mcdib.commands;

import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class CommandPresence extends Command {

    // TODO: Command: Change Bot presence text & type

    public CommandPresence(TextChannel channel) {
        super("presence", 2, 64, channel);
    }

    @Override
    public void run(List<String> args) {
        sendMessageToDiscord("Hi there, you ran the presence command: " + args.toString());
    }

}
