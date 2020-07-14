package not.hub.mcdib.commands;

import com.google.common.collect.Lists;
import not.hub.mcdib.DiscordBot;
import not.hub.mcdib.util.PresenceGenerator;

import java.util.Collections;
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
            sendToDiscord(failMessage);
            return;
        }

        boolean state;
        if (State.ON.getValues().contains(stateInput)) {
            state = true;
        } else if (State.OFF.getValues().contains(stateInput)) {
            state = false;
        } else {
            sendToDiscord(failMessage);
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
                sendToDiscord(failMessage);
                return;
            } else {
                relay = Relay.BOTH;
            }
        }

        if (relay.equals(Relay.DISCORD)) {
            sendToDiscord("Relay Discord -> Minecraft status: " + state);
            getBot().setD2mEnabled(state);
        } else if (relay.equals(Relay.MINECRAFT)) {
            sendToDiscord("Relay Discord <- Minecraft status: " + state);
            getBot().setM2dEnabled(state);
        } else {
            sendToDiscord("Relay Discord -> Minecraft status: " + state + "\n" + "Relay Discord <- Minecraft status: " + state);
            getBot().setD2mEnabled(state);
            getBot().setM2dEnabled(state);
        }

        PresenceGenerator.updatePresence(getBot().getJda().getPresence(), getBot().getD2mEnabled(), getBot().getM2dEnabled());

    }

    private String generateFailMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Wrong input!").append("\n")
                .append(getBot().getCommandPrefix()).append("relay <state>").append("\n")
                .append(getBot().getCommandPrefix()).append("relay <state> <relay>").append("\n");
        Stream.of(State.values()).forEach(state ->
                sb.append(state.toString()).append(": ").append(String.join(", ", state.getValues())).append("\n"));
        Stream.of(Relay.values()).forEach(relay -> {
            if (!relay.equals(Relay.BOTH))
                sb.append(relay.toString()).append(": ").append(String.join(", ", relay.getValues())).append("\n");
        });
        return sb.toString();
    }

    public enum Relay {

        MINECRAFT(Lists.newArrayList("m", "mc", "mine", "minecraft")),
        DISCORD(Lists.newArrayList("d", "dc", "disc", "discord")),
        BOTH(Collections.emptyList());

        private final List<String> values;

        Relay(final List<String> values) {
            this.values = values;
        }

        public List<String> getValues() {
            return values;
        }

    }

    public enum State {

        ON(Lists.newArrayList("on", "yes", "1", "true")),
        OFF(Lists.newArrayList("off", "no", "0", "false"));

        private final List<String> values;

        State(final List<String> values) {
            this.values = values;
        }

        public List<String> getValues() {
            return values;
        }

    }

}
