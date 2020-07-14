package not.hub.mcdib.commands;

import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import not.hub.mcdib.DiscordBot;
import not.hub.mcdib.utils.Log;

import java.util.List;

public class CommandPurge extends Command {

    // TODO: Command: purge chat history (argument: number of messages) or (argument: timestamp start deleterange)

    public CommandPurge(DiscordBot discordBot) {
        super("purge", "purge messages in bridge channel", 0, 1, discordBot);
    }

    @Override
    public void run(List<String> args) {

        TextChannel channel = getBot().getJda().getTextChannelById(getBot().getBridgeChannelId());

        if (channel == null) {
            Log.warn("Unable to find bridge channel!");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args.get(0)) + 1;
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            amount = 2;
        }

        if (amount < 2) {
            new MessageHistory(channel).retrievePast(1).complete().get(0).delete().complete();
            sendToDiscord("purged " + (amount - 1) + " messages \uD83E\uDD21");
            return;
        }

        if (amount > 100) {
            amount = 100;
        }

        channel.deleteMessages(new MessageHistory(channel).retrievePast(amount).complete()).queue();
        sendToDiscord("purged " + (amount == 100 ? amount : amount - 1) + " messages");

    }

}
