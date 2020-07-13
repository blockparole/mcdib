package not.hub.mcdib;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class DiscordBot {

    // m2dQueue & d2mQueue are used for inter thread communication.
    // they should be used in a way that the discord thread can be blocked
    // for a maximum of n ms (is there a discord connection timeout?)
    // but the mc thread will never get blocked by reading or writing the queues.
    // see BlockingQueue javadoc for read/write method explanation.
    private final BlockingQueue<String> m2dQueue;
    private final BlockingQueue<String> d2mQueue;

    private final String token;
    private final String channel;
    private final Set<String> adminUsers;
    private final Set<String> adminRoles;

    public DiscordBot(BlockingQueue<String> m2dQueue, BlockingQueue<String> d2mQueue, String token, String channel, Set<String> adminUsers, Set<String> adminRoles) {

        this.m2dQueue = m2dQueue;
        this.d2mQueue = d2mQueue;

        this.token = token;
        this.channel = channel;
        this.adminUsers = adminUsers;
        this.adminRoles = adminRoles;

        // this returns null if the queue was empty:
        // String testfrommc = m2dQueue.poll();

        // this returns false if the element was not added (queue was full probably):
        // d2mQueue.offer("testfromdiscord");

        // TODO: implement discord bot using https://github.com/Discord4J/Discord4J

    }

    public void shutdown() {
        // TODO: ensure discord4j shutdown
    }

}
