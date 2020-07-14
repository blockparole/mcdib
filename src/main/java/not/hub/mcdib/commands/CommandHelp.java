package not.hub.mcdib.commands;

import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class CommandHelp extends Command {

    // TODO: Command: Help Command (List of Commands & Arguments)

    public CommandHelp(TextChannel channel) {
        super("help", 0, 0, channel);
    }

    @Override
    public void run(List<String> args) {
        sendMessageToDiscord("Hi there, you ran the help command! arguments: " + args.toString());
    }

}
