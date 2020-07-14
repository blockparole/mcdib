package not.hub.mcdib.commands;

import not.hub.mcdib.DiscordBot;

import java.util.List;
import java.util.stream.Collectors;

public class CommandHelp extends Command {

    public CommandHelp(DiscordBot discordBot) {
        super("help", "get some help", 0, 1, discordBot);
    }

    @Override
    public void run(List<String> args) {
        if (args.size() == 0) {
            sendToDiscord("Commands: " + getBot().getCommandProcessor().getCommands().stream().map(Command::getName).collect(Collectors.joining(", ")));
        } else {
            StringBuilder sb = new StringBuilder();
            getBot().getCommandProcessor().getCommands().stream().filter(command -> command.getName().toLowerCase().equals(args.get(0).toLowerCase())).findFirst().ifPresent(command -> {
                sb
                        .append("Name: ")
                        .append(command.getName())
                        .append("\n")
                        .append("Args: ");
                if (getMinArgs() != getMaxArgs()) {
                    sb
                            .append(command.getMinArgs())
                            .append("-")
                            .append(command.getMaxArgs());
                } else {
                    sb
                            .append(command.getMinArgs());
                }
                sb
                        .append("\n")
                        .append("Description: ")
                        .append(command.getDescription());
            });
            sendToDiscord(sb.toString());
        }
    }

}
