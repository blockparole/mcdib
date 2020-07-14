package not.hub.mcdib.commands;

import not.hub.mcdib.DiscordBot;

import java.util.List;

public class CommandFlood extends Command {

    public CommandFlood(DiscordBot discordBot) {
        super("flood", "show message flood level", 0, 0, discordBot);
    }

    @Override
    public void run(List<String> args) {

        sendToDiscord("Discord -> Minecraft average messages per minute: " +
                String.format("%.2f", getBot().getFloodCounter().getD2mMinuteAverage()) + "/" +
                getBot().getFloodCounter().getLimitD2mMessagesPerMinute() +
                ((getBot().getFloodCounter().isD2mFlood()) ? " (flood)" : "") + "\n" +

                "Discord <- Minecraft average messages per minute: " +
                String.format("%.2f", getBot().getFloodCounter().getM2dMinuteAverage()) + "/" +
                getBot().getFloodCounter().getLimitM2dMessagesPerMinute() +
                ((getBot().getFloodCounter().isM2dFlood()) ? " (flood)" : "")
        );

    }

}
