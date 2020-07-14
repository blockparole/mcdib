package not.hub.mcdib.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import not.hub.mcdib.DiscordBot;
import not.hub.mcdib.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandProcessor {

    private final Set<Command> commands;

    public CommandProcessor(TextChannel channel, DiscordBot discordBot) {
        commands = new HashSet<>();
        // TODO: remove circular dependency commands have for discordBot
        loadCommands(discordBot);
    }

    private void loadCommands(DiscordBot discordBot) {
        commands.add(new CommandHelp(discordBot));
        commands.add(new CommandPresence(discordBot));
        commands.add(new CommandPurge(discordBot));
        commands.add(new CommandRelay(discordBot));
        commands.add(new CommandSlowmode(discordBot));
        commands.add(new CommandTellraw(discordBot));
    }

    public void processCommand(String command, List<String> args) {
        Log.info("Detected command: " + command + " with arguments: " + args.toString());
        commands.stream().filter(com ->
                com.getName().toLowerCase().equals(command.toLowerCase())
                        && com.getMinArgs() <= args.size()
                        && com.getMaxArgs() >= args.size()
        ).findFirst().ifPresent(com -> com.run(args));
    }

    public Set<Command> getCommands() {
        return commands;
    }

}
