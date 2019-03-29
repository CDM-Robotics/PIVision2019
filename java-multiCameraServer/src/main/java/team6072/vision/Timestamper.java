package team6072.vision;

public class Timestamper {

    private static Timestamper mTimestamper;
    private static long mStartTime;

    private static long mT1;
    private static long mT2;
    private static TimeStamp mTimeStamp;

    private Timestamper() {
        mStartTime = System.currentTimeMillis();
        mTimeStamp = TimeStamp.Time1;
        System.out.printf("Starting TimeStamper: currentTime = %d", mStartTime);
    }

    public static Timestamper getInstance() {
        if (mTimestamper == null) {
            mTimestamper = new Timestamper();
        }
        return mTimestamper;
    }

    public void recordTime1() {
        if (mTimeStamp == TimeStamp.Time1) {
            mT1 = System.currentTimeMillis();
            mTimeStamp = TimeStamp.Time2;
        }
    }

    public void recordTime2() {
        if (mTimeStamp == TimeStamp.Time2) {
            mT2 = System.currentTimeMillis();
            mTimeStamp = TimeStamp.Time1;
        }
    }

    public long getRunTime() {
        if (mTimeStamp == TimeStamp.Time1) {
            return (mT2 - mT1);
        } else {
            System.out.println("ERROR: Invalid Timestamp - You got the order wrong somehow!");
            return -1;
        }
    }

    private enum TimeStamp {
        Time1, Time2;
    }

}