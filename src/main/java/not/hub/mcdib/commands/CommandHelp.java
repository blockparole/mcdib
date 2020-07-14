package not.hub.mcdib.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import not.hub.mcdib.DiscordBot;

import java.util.List;
import java.util.stream.Collectors;

public class CommandHelp extends Command {

    // TODO: Command: Help Command (List of Commands & Arguments)

    public CommandHelp(TextChannel channel, DiscordBot discordBot) {
        super("help", 0, 0, channel, discordBot);
    }

    @Override
    public void run(List<String> args) {
        sendMessageToDiscord("Commands: " + getBot().getCommandProcessor().getCommands().stream().map(Command::getName).collect(Collectors.joining(", ")));
    }

}
