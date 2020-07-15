package not.hub.mcdib.commands;

import not.hub.mcdib.DiscordBot;
import not.hub.mcdib.messages.InfoMessage;

import java.util.List;

public abstract class Command {

    private final String name;
    private final String description;

    // TODO: replace min and max ints with int ranges so we can have commands with dynamic arg count
    private final int minArgs;
    private final int maxArgs;

    private final DiscordBot bot;

    public Command(String name, String description, int minArgs, int maxArgs, DiscordBot bot) {
        this.name = name;
        this.description = description;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.bot = bot;
    }

    /**
     * Extending commands want to override this to run logic on command call
     */
    public void run(List<String> args) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return name.equals(command.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public void sendInfoToDiscord(String message) {
        bot.sendMessageToDiscord(new InfoMessage(message));
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getMinArgs() {
        return minArgs;
    }

    public int getMaxArgs() {
        return maxArgs;
    }

    DiscordBot getBot() {
        return bot;
    }

}
