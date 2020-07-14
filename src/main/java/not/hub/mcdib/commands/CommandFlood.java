package not.hub.mcdib.commands;

import not.hub.mcdib.AntiFlood;
import not.hub.mcdib.DiscordBot;
import not.hub.mcdib.enums.Relay;

import java.util.List;
import java.util.stream.Stream;

public class CommandFlood extends Command {

    public CommandFlood(DiscordBot discordBot) {
        super("flood", "control message drop in case of flood", 0, 2, discordBot);
    }

    @Override
    public void run(List<String> args) {

        if (args.size() == 0) {
            sendToDiscord(generateInfoMessage());
            return;
        }

        if (args.size() != 2) {
            sendToDiscord(generateFailMessage());
            return;
        }

        Relay relay;
        String relayInput = args.get(0).toLowerCase();
        if (Relay.DISCORD.getValues().contains(relayInput)) {
            relay = Relay.DISCORD;
        } else if (Relay.MINECRAFT.getValues().contains(relayInput)) {
            relay = Relay.MINECRAFT;
        } else {
            sendToDiscord(generateFailMessage());
            return;
        }

        int messagesPerSecond;
        try {
            messagesPerSecond = Integer.parseInt(args.get(1));
        } catch (NumberFormatException e) {
            sendToDiscord(generateFailMessage());
            return;
        }

        if (messagesPerSecond < 1 || messagesPerSecond > 60) {
            sendToDiscord(generateFailMessage());
            return;
        }

        if (relay.equals(Relay.DISCORD)) {
            getBot().getAntiFlood().setD2mMinuteAverageLimit(messagesPerSecond);
            sendToDiscord("Discord to Minecraft Antiflood limit is now: " + messagesPerSecond + " messages per second");
        } else {
            getBot().getAntiFlood().setM2dMinuteAverageLimit(messagesPerSecond);
            sendToDiscord("Minecraft to Discord Antiflood limit is now: " + messagesPerSecond + " messages per second");
        }

    }

    private String generateInfoMessage() {
        AntiFlood antiFlood = getBot().getAntiFlood();
        return "Discord -> Minecraft average messages per minute: "
                + antiFlood.getD2mMinuteAverage() + "/"
                + antiFlood.getD2mMinuteAverageLimit()
                + ((antiFlood.isD2mFlood()) ? ((antiFlood.isActive()) ? " (flood! currently dropping messages)" : " (flood!)") : "") + "\n"
                + "Discord <- Minecraft average messages per minute: "
                + antiFlood.getM2dMinuteAverage() + "/"
                + antiFlood.getM2dMinuteAverageLimit()
                + ((antiFlood.isM2dFlood()) ? ((antiFlood.isActive()) ? " (flood! currently dropping messages)" : " (flood!)") : "") + "\n"
                + "Antiflood status: "
                + (getBot().getAntiFlood().isActive() ? "enabled" : "disabled");
    }

    private String generateFailMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Wrong input!").append("\n")
                .append(getBot().getCommandPrefix()).append("flood <state>").append("\n")
                .append(getBot().getCommandPrefix()).append("flood <relay> <limit>").append("\n");
        Stream.of(Relay.values()).forEach(relay -> {
            if (!relay.equals(Relay.BOTH)) {
                sb.append(relay.toString()).append(": ").append(String.join(", ", relay.getValues())).append("\n");
            }
        });
        sb.append("LIMIT: 1 - 60");
        return sb.toString();
    }

}
