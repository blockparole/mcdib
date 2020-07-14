package not.hub.mcdib;

import not.hub.mcdib.messages.ChatMessage;
import not.hub.mcdib.utils.PresenceGenerator;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class AntiFlood {

    private final DiscordBot bot;

    private final Timer timer;

    private final int[] d2mRing;
    private final int[] m2dRing;

    private int ringIndex;

    private int d2mMessagesPerSecondCounter;
    private int m2dMessagesPerSecondCounter;

    private boolean active;

    private int d2mMinuteAverage;
    private int m2dMinuteAverage;

    private int d2mMinuteAverageLimit = 30;
    private int m2dMinuteAverageLimit = 60;

    private boolean d2mFlood = false;
    private boolean m2dFlood;

    public AntiFlood(boolean active, DiscordBot bot) {
        this.active = active;
        this.bot = bot;
        this.d2mRing = new int[60];
        this.m2dRing = new int[60];
        this.timer = new Timer();
        init();
    }

    private void init() {
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                d2mRing[ringIndex] = d2mMessagesPerSecondCounter;
                d2mMessagesPerSecondCounter = 0;

                m2dRing[ringIndex] = m2dMessagesPerSecondCounter;
                m2dMessagesPerSecondCounter = 0;

                ringIndex++;

                if (ringIndex >= d2mRing.length || ringIndex >= m2dRing.length) {
                    ringIndex = 0;
                }

                d2mMinuteAverage = Arrays.stream(d2mRing).sum();
                boolean d2mFloodOld = d2mFlood;
                d2mFlood = d2mMinuteAverage > d2mMinuteAverageLimit;
                if (d2mFlood != d2mFloodOld && active) {
                    bot.sendMessageToDiscord(new ChatMessage("Antiflood discord to minecraft message drop is now " + (d2mFlood ? "enabled!" : "disabled again")));
                    PresenceGenerator.updatePresence(bot);
                }

                m2dMinuteAverage = Arrays.stream(m2dRing).sum();
                boolean m2dFloodOld = m2dFlood;
                m2dFlood = m2dMinuteAverage > m2dMinuteAverageLimit;
                if (m2dFlood != m2dFloodOld && active) {
                    bot.sendMessageToDiscord(new ChatMessage("Antiflood minecraft to discord message drop is now " + (m2dFlood ? "enabled!" : "disabled again")));
                    PresenceGenerator.updatePresence(bot);
                }

            }
        }, 0, 1000);

    }

    public void icrementD2mCounter() {
        d2mMessagesPerSecondCounter++;
    }

    public void icrementM2dCounter() {
        m2dMessagesPerSecondCounter++;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isD2mFlood() {
        return d2mFlood;
    }

    public boolean isM2dFlood() {
        return m2dFlood;
    }

    public double getD2mMinuteAverage() {
        return d2mMinuteAverage;
    }

    public double getM2dMinuteAverage() {
        return m2dMinuteAverage;
    }

    public int getD2mMinuteAverageLimit() {
        return d2mMinuteAverageLimit;
    }

    public void setD2mMinuteAverageLimit(int d2mMinuteAverageLimit) {
        this.d2mMinuteAverageLimit = d2mMinuteAverageLimit;
    }

    public int getM2dMinuteAverageLimit() {
        return m2dMinuteAverageLimit;
    }

    public void setM2dMinuteAverageLimit(int m2dMinuteAverageLimit) {
        this.m2dMinuteAverageLimit = m2dMinuteAverageLimit;
    }

}
