package team6072.vision;

import com.google.gson.JsonObject;
import team6072.vision.NetTblConfig;

public class CameraData{

    private double mXDistInches;
    private double mYDistInches;

    public CameraData(){
        mXDistInches = 0;
        mYDistInches = 0;
        CameraMaster.getInstance().addCameraData(this);
    }

    public void updateJson(double KV_X_DIST, double KV_Y_DIST){
        mXDistInches = KV_X_DIST;
        mYDistInches = KV_Y_DIST;
    }

    public double getKV_X_DIST(){
        return mXDistInches;
    }
    public double getKV_Y_DIST(){
        return mYDistInches;
    }
}