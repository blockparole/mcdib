package not.hub.mcdib.commands;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import not.hub.mcdib.DiscordBot;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CommandRelay extends Command {

    public CommandRelay(DiscordBot discordBot) {
        super("relay", "turn chat relay on / off", 1, 2, discordBot);
    }

    @Override
    public void run(List<String> args) {

        String stateInput = args.get(0).toLowerCase();

        final String failMessage =
                "Wrong input! Try one of the following:\n"
                        + getBot().getCommandPrefix() + "relay off discord" + "\n"
                        + getBot().getCommandPrefix() + "relay false disc" + "\n"
                        + getBot().getCommandPrefix() + "relay 1 mc" + "\n"
                        + getBot().getCommandPrefix() + "relay on" + "\n"
                        + getBot().getCommandPrefix() + "relay no";

        if (stateInput.isEmpty()) {
            sendToDiscord(failMessage);
            return;
        }

        boolean state;
        if (State.ON.getPair().contains(stateInput)) {
            state = true;
        } else if (State.OFF.getPair().contains(stateInput)) {
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
            if (Relay.DISCORD.getSet().contains(relayInput)) {
                relay = Relay.DISCORD;
            } else if (Relay.MINECRAFT.getSet().contains(relayInput)) {
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

        boolean d2m = getBot().getD2mEnabled();
        boolean m2d = getBot().getM2dEnabled();

        if (d2m || m2d) {
            getBot().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching("DC " + (m2d ? "<" : "|") + "-" + (d2m ? ">" : "|") + " MC"));
        } else {
            getBot().getJda().getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.of(Activity.ActivityType.WATCHING, "counting electric sheep \uD83D\uDE34"));
        }

    }

    public enum Relay {

        MINECRAFT(Sets.newHashSet("m", "mc", "mine", "minecraft")),
        DISCORD(Sets.newHashSet("d", "dc", "dis", "disc", "discord")),
        BOTH(Collections.emptySet());

        private final Set<String> relay;

        Relay(Set<String> relay) {
            this.relay = relay;
        }

        public Set<String> getSet() {
            return relay;
        }

    }

    public enum State {

        ON(Sets.newHashSet("on", "yes", "1", "true")),
        OFF(Sets.newHashSet("off", "no", "0", "false"));

        private final Set<String> state;

        State(Set<String> state) {
            this.state = state;
        }

        public Set<String> getPair() {
            return state;
        }

    }

}
