package team6072.vision;

import edu.wpi.first.cameraserver.CameraServer;

public class HSVThresholdOutput {

    private static HSVThresholdOutput mHsvThresholdOutput;
    private static int mNumOfOutputs;
    private CameraServer mCameraServer;

    public static HSVThresholdOutput getInstance() {
        if (mHsvThresholdOutput == null) {
            mHsvThresholdOutput = new HSVThresholdOutput();
            mNumOfOutputs = 0;
        }
        mNumOfOutputs++;
        return mHsvThresholdOutput;
    }

    private HSVThresholdOutput() {
        mCameraServer = CameraServer.getInstance();

    }

    private int getNumOfCurrentOutputs(){
        return mNumOfOutputs;
    }

    public String getHSVName(){
        String s = "HSVThresholdOutput " + getNumOfCurrentOutputs();
        return s;
    }
}
