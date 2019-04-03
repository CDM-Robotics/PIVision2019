package team6072.vision;

import com.google.gson.JsonObject;
import team6072.vision.NetTblConfig;

public class CameraData{

    private double mXDistInches;
    private double mYDistInches;
    private boolean mHaveTarget;

    public CameraData(){
        mXDistInches = 0;
        mYDistInches = 0;
        CameraMaster.getInstance().addCameraData(this);
    }

    public void updateData(double x_dist, double y_dist, boolean HaveVision){
        mXDistInches = x_dist;
        mYDistInches = y_dist;
        mHaveTarget = HaveVision;
    }

    public void setHaveTarget(boolean haveTarget){
        mHaveTarget = haveTarget;
    }

    public boolean getHaveTarget(){
        return mHaveTarget;
    }

    public double get_X_DIST(){
        return mXDistInches;
    }
    public double get_Y_DIST(){
        return mYDistInches;
    }
}