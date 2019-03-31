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

    public void updateData(double KV_X_DIST, double KV_Y_DIST, boolean HaveVision){
        mXDistInches = KV_X_DIST;
        mYDistInches = KV_Y_DIST;
        mHaveTarget = HaveVision;
    }

    public void updateData(boolean HaveTarget){
        mHaveTarget = HaveTarget;
    }

    public boolean isHaveTarget(){
        return mHaveTarget;
    }

    public double getKV_X_DIST(){
        return mXDistInches;
    }
    public double getKV_Y_DIST(){
        return mYDistInches;
    }
}