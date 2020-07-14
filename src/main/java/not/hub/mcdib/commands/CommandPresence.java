package not.hub.mcdib.commands;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import not.hub.mcdib.DiscordBot;

import java.util.List;

public class CommandPresence extends Command {

    // TODO: Command: Change Bot presence text & type

    public CommandPresence(TextChannel channel, DiscordBot discordBot) {
        super("presence", 1, 64, channel, discordBot);
    }

    @Override
    public void run(List<String> args) {
        getBot().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching(String.join(" ", args)));
    }

}
