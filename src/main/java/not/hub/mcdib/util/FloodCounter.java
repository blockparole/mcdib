package not.hub.mcdib.util;

import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.Timer;
import java.util.TimerTask;

public class FloodCounter {

    private final Timer timer;
    private final int[] d2mRing;
    private final int[] m2dRing;

    private int ringIndex;

    private int limitD2mMessagesPerMinute = 15;
    private int limitM2dMessagesPerMinute = 15;

    private int d2mMessagesPerSecondCounter;
    private int m2dMessagesPerSecondCounter;

    private double d2mMinuteAverage;
    private double m2dMinuteAverage;

    private boolean d2mFlood;
    private boolean m2dFlood;

    private boolean active;

    public FloodCounter(boolean active) {
        this.active = active;
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

                OptionalDouble d2mMinuteAverageOptional = Arrays.stream(d2mRing).average();
                if (d2mMinuteAverageOptional.isPresent()) {
                    d2mMinuteAverage = d2mMinuteAverageOptional.getAsDouble();
                } else {
                    d2mMinuteAverage = 0;
                }
                d2mFlood = d2mMinuteAverage > limitD2mMessagesPerMinute;

                OptionalDouble m2dMinuteAverageOptional = Arrays.stream(m2dRing).average();
                if (m2dMinuteAverageOptional.isPresent()) {
                    m2dMinuteAverage = m2dMinuteAverageOptional.getAsDouble();
                } else {
                    m2dMinuteAverage = 0;
                }
                m2dFlood = m2dMinuteAverage > limitM2dMessagesPerMinute;

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

    public double getD2mMinuteAverage() {
        return d2mMinuteAverage;
    }

    public double getM2dMinuteAverage() {
        return m2dMinuteAverage;
    }

    public int getLimitD2mMessagesPerMinute() {
        return limitD2mMessagesPerMinute;
    }

    public void setLimitD2mMessagesPerMinute(int limitD2mMessagesPerMinute) {
        this.limitD2mMessagesPerMinute = limitD2mMessagesPerMinute;
    }

    public int getLimitM2dMessagesPerMinute() {
        return limitM2dMessagesPerMinute;
    }

    public void setLimitM2dMessagesPerMinute(int limitM2dMessagesPerMinute) {
        this.limitM2dMessagesPerMinute = limitM2dMessagesPerMinute;
    }

    public boolean isD2mFlood() {
        return active && d2mFlood;
    }

    public boolean isM2dFlood() {
        return active && m2dFlood;
    }

}
