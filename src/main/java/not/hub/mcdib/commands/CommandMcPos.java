package not.hub.mcdib.commands;

import not.hub.mcdib.DiscordBot;

import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class CommandMcPos extends Command {

    private static final Pattern REGEX_PATTERN_MC_USER = Pattern.compile("^\\w{3,16}$");

    public CommandMcPos(DiscordBot discordBot) {
        super("mcpos", "calculates the position of a minecraft player based on advanced algorithms", 1, 1, discordBot);
    }

    @Override
    public void run(List<String> args) {

        if (!REGEX_PATTERN_MC_USER.matcher(args.get(0)).matches()) {
            sendInfoToDiscord("Are you sure thats a valid minecraft username?");
            return;
        }

        char[] name = args.get(0).toCharArray();

        long hashLong = 0;
        for (char c : args.get(0).toCharArray()) {
            hashLong = 31L * hashLong + c;
        }

        // TODO: https://docs.oracle.com/javase/8/docs/api/java/util/Random.html#nextGaussian--
        Random rand = new Random(hashLong);
        int xPos = rand.nextInt(30000000 - (-30000000)) + (-30000000);
        xPos = Math.floorDiv(xPos, 3);
        int yPos = rand.nextInt(30000000 - (-30000000)) + (-30000000);
        yPos = Math.floorDiv(yPos, 4);

        StringBuilder sb = new StringBuilder();

        if (name[0] % 2 == 0) {
            sb.append("Base");
        } else {
            if (name[1] % 2 == 0) {
                sb.append("Stash");
            } else {
                sb.append("Bed");
            }
        }

        sb
                .append(" Cords for Player ")
                .append(args.get(0))
                .append(" are: X")
                .append(xPos)
                .append(" Y")
                .append(yPos);

        sendInfoToDiscord(sb.toString());

    }

}
