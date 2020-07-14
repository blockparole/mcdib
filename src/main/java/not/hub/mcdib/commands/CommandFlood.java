package not.hub.mcdib.commands;

import not.hub.mcdib.AntiFlood;
import not.hub.mcdib.DiscordBot;

import java.util.List;

public class CommandFlood extends Command {

    public CommandFlood(DiscordBot discordBot) {
        super("flood", "control message drop in case of flood", 0, 0, discordBot);
    }

    @Override
    public void run(List<String> args) {

        AntiFlood antiFlood = getBot().getAntiFlood();

        sendToDiscord("Discord -> Minecraft average messages per minute: "
                + antiFlood.getD2mMinuteAverage() + "/"
                + antiFlood.getD2mMinuteAverageLimit()
                + ((antiFlood.isD2mFlood()) ? ((antiFlood.isActive()) ? " (flood! currently dropping messages)" : " (flood!)") : "") + "\n"
                + "Discord <- Minecraft average messages per minute: "
                + antiFlood.getM2dMinuteAverage() + "/"
                + antiFlood.getM2dMinuteAverageLimit()
                + ((antiFlood.isM2dFlood()) ? ((antiFlood.isActive()) ? " (flood! currently dropping messages)" : " (flood!)") : "") + "\n"
                + "Antiflood status: "
                + (getBot().getAntiFlood().isActive() ? "enabled" : "disabled")
        );

    }

}
