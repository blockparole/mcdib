package not.hub.mcdib.commands;

import not.hub.mcdib.DiscordBot;
import not.hub.mcdib.messages.ChatMessage;

import java.util.List;

public class CommandTellraw extends Command {

    // TODO: Command: tellraw style message sender

    public CommandTellraw(DiscordBot discordBot) {
        super("tellraw", "send raw chat messages to minecraft", 1, 64, discordBot);
    }

    @Override
    public void run(List<String> args) {
        getBot().sendMessageToMinecraft(new ChatMessage(String.join(" ", args)));
    }

}
