package not.hub.mcdib;

import not.hub.mcdib.enums.Relay;
import not.hub.mcdib.messages.InfoMessage;
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

    private int d2mMinuteAverage;
    private int m2dMinuteAverage;

    private boolean d2mAntifloodActive;
    private boolean m2dAntifloodActive;

    private boolean d2mFloodThresholdReached;
    private boolean m2dFloodThresholdReached;

    private int d2mMinuteAverageLimit;
    private int m2dMinuteAverageLimit;

    public AntiFlood(boolean d2mAntifloodActive, boolean m2dAntifloodActive, int d2mMinuteAverageLimit, int m2dMinuteAverageLimit, DiscordBot bot) {
        this.d2mAntifloodActive = d2mAntifloodActive;
        this.m2dAntifloodActive = m2dAntifloodActive;
        this.d2mMinuteAverageLimit = d2mMinuteAverageLimit;
        this.m2dMinuteAverageLimit = m2dMinuteAverageLimit;
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

                // TODO: BUG: if floodThresholdReached is true antifloodActive is false
                // when setting antifloodActive to true (CommandFlood -flood dc yes), the call of sendAntiFloodPauseMessage is skipped!

                d2mMinuteAverage = Arrays.stream(d2mRing).sum();
                boolean d2mFloodThresholdReachedOld = d2mFloodThresholdReached;
                d2mFloodThresholdReached = d2mMinuteAverage > d2mMinuteAverageLimit;
                if (d2mFloodThresholdReached != d2mFloodThresholdReachedOld) {
                    if (d2mAntifloodActive) {
                        sendAntiFloodPauseMessage(Relay.DISCORD, d2mFloodThresholdReached);
                    } else if (!d2mFloodThresholdReached) {
                        sendAntiFloodPauseMessage(Relay.DISCORD, d2mFloodThresholdReached);
                    }
                }

                m2dMinuteAverage = Arrays.stream(m2dRing).sum();
                boolean m2dFloodThresholdReachedOld = m2dFloodThresholdReached;
                m2dFloodThresholdReached = m2dMinuteAverage > m2dMinuteAverageLimit;
                if (m2dFloodThresholdReached != m2dFloodThresholdReachedOld) {
                    if (m2dAntifloodActive) {
                        sendAntiFloodPauseMessage(Relay.MINECRAFT, m2dFloodThresholdReached);
                    } else if (!m2dFloodThresholdReached) {
                        sendAntiFloodPauseMessage(Relay.MINECRAFT, m2dFloodThresholdReached);
                    }
                }

            }
        }, 0, 1000);

    }

    private void sendAntiFloodPauseMessage(Relay relay, boolean d2mFloodThresholdReached) {
        bot.sendMessageToDiscord(new InfoMessage("Antiflood "
                + (relay.equals(Relay.DISCORD) ? "discord to minecraft" : "minecraft to discord")
                + " relay pause " + (d2mFloodThresholdReached ? "enabled!" : "disabled again")));
        PresenceGenerator.updatePresence(bot);
    }

    public void icrementD2mCounter() {
        d2mMessagesPerSecondCounter++;
    }

    public void icrementM2dCounter() {
        m2dMessagesPerSecondCounter++;
    }

    public boolean shouldDropD2mChatMessages() {
        return d2mAntifloodActive && d2mFloodThresholdReached;
    }

    public boolean shouldDropM2dChatMessages() {
        return m2dAntifloodActive && m2dFloodThresholdReached;
    }

    public boolean isD2mAntifloodActive() {
        return d2mAntifloodActive;
    }

    public void setD2mAntifloodActive(boolean d2mAntifloodActive) {
        this.d2mAntifloodActive = d2mAntifloodActive;
    }

    public boolean isM2dAntifloodActive() {
        return m2dAntifloodActive;
    }

    public void setM2dAntifloodActive(boolean m2dAntifloodActive) {
        this.m2dAntifloodActive = m2dAntifloodActive;
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
