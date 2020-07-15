package not.hub.mcdib.commands;

import not.hub.mcdib.DiscordBot;
import not.hub.mcdib.enums.Relay;
import not.hub.mcdib.enums.State;
import not.hub.mcdib.messages.ConfigMessage;
import not.hub.mcdib.utils.PresenceGenerator;

import java.util.List;
import java.util.stream.Stream;

public class CommandRelay extends Command {

    private final String failMessage;

    public CommandRelay(DiscordBot discordBot) {
        super("relay", "turn chat relays on / off", 1, 2, discordBot);
        failMessage = generateFailMessage();
    }

    @Override
    public void run(List<String> args) {

        String stateInput = args.get(0).toLowerCase();

        if (stateInput.isEmpty()) {
            sendInfoToDiscord(failMessage);
            return;
        }

        State state;
        if (State.ON.getValues().contains(stateInput)) {
            state = State.ON;
        } else if (State.OFF.getValues().contains(stateInput)) {
            state = State.OFF;
        } else {
            sendInfoToDiscord(failMessage);
            return;
        }

        Relay relay;
        if (args.size() < 2) {
            relay = Relay.BOTH;
        } else {
            String relayInput = args.get(1).toLowerCase();
            if (Relay.DISCORD.getValues().contains(relayInput)) {
                relay = Relay.DISCORD;
            } else if (Relay.MINECRAFT.getValues().contains(relayInput)) {
                relay = Relay.MINECRAFT;
            } else if (!relayInput.isEmpty()) {
                sendInfoToDiscord(failMessage);
                return;
            } else {
                relay = Relay.BOTH;
            }
        }

        if (relay.equals(Relay.DISCORD)) {
            sendInfoToDiscord("Relay Discord -> Minecraft status: " + state);
            getBot().sendConfigMessage(new ConfigMessage("relay-d2m", state.getValueString()));
            getBot().setD2mEnabled(state.getState());
        } else if (relay.equals(Relay.MINECRAFT)) {
            sendInfoToDiscord("Relay Discord <- Minecraft status: " + state);
            getBot().sendConfigMessage(new ConfigMessage("relay-m2d", state.getValueString()));
            getBot().setM2dEnabled(state.getState());
        } else {
            sendInfoToDiscord("Relay Discord -> Minecraft status: " + state + "\n" + "Relay Discord <- Minecraft status: " + state);
            getBot().sendConfigMessage(new ConfigMessage("relay-d2m", state.getValueString()));
            getBot().sendConfigMessage(new ConfigMessage("relay-m2d", state.getValueString()));
            getBot().setD2mEnabled(state.getState());
            getBot().setM2dEnabled(state.getState());
        }

        PresenceGenerator.updatePresence(getBot());

    }

    private String generateFailMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Wrong input!").append("\n")
                .append(getBot().getCommandPrefix()).append("relay <state>").append("\n")
                .append(getBot().getCommandPrefix()).append("relay <state> <relay>").append("\n");
        Stream.of(State.values()).forEach(state ->
                sb.append(state.toString()).append(": ").append(String.join(", ", state.getValues())).append("\n"));
        Stream.of(Relay.values()).forEach(relay -> {
            if (!relay.equals(Relay.BOTH)) {
                sb.append(relay.toString()).append(": ").append(String.join(", ", relay.getValues())).append("\n");
            }
        });
        return sb.toString();
    }

}
