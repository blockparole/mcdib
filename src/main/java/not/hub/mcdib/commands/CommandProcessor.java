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
        loadCommands(channel, discordBot);
    }

    private void loadCommands(TextChannel channel, DiscordBot discordBot) {
        commands.add(new CommandHelp(channel, discordBot));
        commands.add(new CommandPresence(channel, discordBot));
        commands.add(new CommandPurge(channel, discordBot));
        commands.add(new CommandRelay(channel, discordBot));
    }

    public void processCommand(String command, List<String> args) {
        Log.info("Detected command: " + command + " with arguments: " + args.toString());
        commands.stream().filter(com ->
                com.name.toLowerCase().equals(command.toLowerCase())
                        && com.minArgs <= args.size()
                        && com.maxArgs >= args.size()
        ).findFirst().ifPresent(com -> com.run(args));
    }

    public Set<Command> getCommands() {
        return commands;
    }

}
