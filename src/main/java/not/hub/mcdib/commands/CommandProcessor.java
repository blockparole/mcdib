package not.hub.mcdib.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import not.hub.mcdib.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandProcessor {

    private final TextChannel channel;
    private final Set<Command> commands;

    public CommandProcessor(TextChannel channel) {
        this.channel = channel;
        commands = new HashSet<>();
        loadCommands();
    }

    private void loadCommands() {
        commands.add(new CommandHelp(channel));
        commands.add(new CommandPresence(channel));
        commands.add(new CommandPurge(channel));
        commands.add(new CommandRelay(channel));
    }

    public void processCommand(String command, List<String> args) {
        Log.info("Detected command: " + command + " with arguments: " + args.toString());
        commands.stream().filter(com ->
                com.name.toLowerCase().equals(command.toLowerCase())
                        && com.minArgs <= args.size()
                        && com.maxArgs >= args.size()
        ).findFirst().ifPresent(com -> com.run(args));
    }

}
