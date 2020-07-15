package not.hub.mcdib.commands;

import not.hub.mcdib.AntiFlood;
import not.hub.mcdib.DiscordBot;
import not.hub.mcdib.enums.Relay;
import not.hub.mcdib.enums.State;

import java.util.List;
import java.util.stream.Stream;

public class CommandFlood extends Command {

    public CommandFlood(DiscordBot discordBot) {
        super("flood", "control message drop in case of flood", 0, 2, discordBot);
    }

    @Override
    public void run(List<String> args) {

        if (args.size() == 0) {
            sendInfoToDiscord(generateInfoMessage());
            return;
        }

        if (args.size() != 2) {
            sendInfoToDiscord(generateFailMessage());
            return;
        }

        boolean isNoRelay = false;
        Relay relay;
        String relayInput = args.get(0).toLowerCase();
        // TODO: Implement Relay.BOTH
        if (Relay.DISCORD.getValues().contains(relayInput)) {
            relay = Relay.DISCORD;
        } else if (Relay.MINECRAFT.getValues().contains(relayInput)) {
            relay = Relay.MINECRAFT;
        } else {
            relay = null;
            isNoRelay = true;
        }

        boolean isNoNumber = false;
        int messagesPerSecond = 0;
        try {
            messagesPerSecond = Integer.parseInt(args.get(1));
        } catch (NumberFormatException e) {
            isNoNumber = true;
        }

        if (!isNoNumber && (messagesPerSecond < 1 || messagesPerSecond > 60)) {
            sendInfoToDiscord(generateFailMessage());
            return;
        }

        boolean isNoBoolean = false;
        State state;
        if (State.ON.getValues().contains(args.get(1))) {
            state = State.ON;
        } else if (State.OFF.getValues().contains(args.get(1))) {
            state = State.OFF;
        } else {
            state = null;
            isNoBoolean = true;
        }

        if (isNoRelay || isNoNumber && isNoBoolean) {
            sendInfoToDiscord(generateFailMessage());
            return;
        }

        if (!isNoNumber) {
            // number value
            if (relay.equals(Relay.DISCORD)) {
                getBot().getAntiFlood().setD2mMinuteAverageLimit(messagesPerSecond);
                sendInfoToDiscord("Discord to Minecraft Antiflood limit is now: " + messagesPerSecond + " messages per second");
            } else {
                getBot().getAntiFlood().setM2dMinuteAverageLimit(messagesPerSecond);
                sendInfoToDiscord("Minecraft to Discord Antiflood limit is now: " + messagesPerSecond + " messages per second");
            }
        } else {
            // boolean value
            if (relay.equals(Relay.DISCORD)) {
                getBot().getAntiFlood().setD2mAntifloodActive(state.getState());
                sendInfoToDiscord("Discord to Minecraft Antiflood is now: " + (state.getState() ? "enabled" : "disabled"));
            } else {
                getBot().getAntiFlood().setM2dAntifloodActive(state.getState());
                sendInfoToDiscord("Minecraft to Discord Antiflood is now: " + (state.getState() ? "enabled" : "disabled"));
            }
        }

    }

    private String generateInfoMessage() {
        AntiFlood antiFlood = getBot().getAntiFlood();
        return "Discord -> Minecraft average messages per minute: "
                + antiFlood.getD2mMinuteAverage() + "/"
                + antiFlood.getD2mMinuteAverageLimit() + "\n"
                + "(Status: " + (antiFlood.isD2mAntifloodActive() ? "enabled" : "disabled") + ")"
                + ((antiFlood.shouldDropD2mChatMessages()) ? " (flood! currently dropping messages)" : " ") + "\n"
                + "Discord <- Minecraft average messages per minute: "
                + antiFlood.getM2dMinuteAverage() + "/"
                + antiFlood.getM2dMinuteAverageLimit() + "\n"
                + "(Status: " + (antiFlood.isM2dAntifloodActive() ? "enabled" : "disabled") + ")"
                + ((antiFlood.shouldDropM2dChatMessages()) ? " (flood! currently dropping messages)" : " ") + "";
    }

    private String generateFailMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Wrong input!").append("\n")
                .append(getBot().getCommandPrefix()).append("flood <relay> <state>").append("\n")
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
