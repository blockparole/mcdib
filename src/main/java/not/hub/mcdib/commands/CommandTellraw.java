package not.hub.mcdib.commands;

import not.hub.mcdib.DiscordBot;
import not.hub.mcdib.messages.RawMessage;

import java.util.List;

public class CommandTellraw extends Command {

    public CommandTellraw(DiscordBot discordBot) {
        super("tellraw", "send raw chat messages to minecraft", 1, 64, discordBot);
    }

    @Override
    public void run(List<String> args) {
        getBot().sendMessageToMinecraft(new RawMessage(String.join(" ", args)));
    }

}
