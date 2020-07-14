package not.hub.mcdib.commands;

import not.hub.mcdib.DiscordBot;
import not.hub.mcdib.message.ChatMessage;
import not.hub.mcdib.util.Log;

import java.util.List;

public class CommandTellraw extends Command {

    // TODO: Command: tellraw style message sender

    public CommandTellraw(DiscordBot discordBot) {
        super("tellraw", "send raw chat messages to minecraft", 1, 64, discordBot);
    }

    @Override
    public void run(List<String> args) {
        if (!getBot().getD2mQueue().offer(new ChatMessage(String.join(" ", args)))) {
            Log.warn("Unable to insert Discord tellraw message into Minecraft send queue, message dropped...");
        }
    }

}
