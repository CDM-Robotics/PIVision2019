package team6072.vision;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import team6072.vision.NetTblConfig;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;

public class CameraMaster {

    private static CameraMaster mCameraMaster;
    private static ArrayList<CameraData> cameraDatas;
    private static NetworkTableEntry mXDist;
    private static NetworkTableEntry mYDist;
    private static NetworkTableEntry mHaveTarget;
    private static NetworkTableEntry mBlownUp;
    private static NetworkTableEntry mTargetAngle;
    private static NetworkTableEntry mCurrentAngle;
    private static NetworkTable mTable;

    private final double ERROR_CORRECTION_CONSTANT = 1.2857;

    private CameraMaster() {
        cameraDatas = new ArrayList<CameraData>();
        NetworkTableInstance mNetworkInstance = NetworkTableInstance.getDefault();
        mTable = mNetworkInstance.getTable(NetTblConfig.T_VISION);
        mXDist = mTable.getEntry(NetTblConfig.KV_X_DIST);
        mYDist = mTable.getEntry(NetTblConfig.KV_Y_DIST);
        mHaveTarget = mTable.getEntry(NetTblConfig.KV_HAVE_TARGET);
        mTargetAngle = mTable.getEntry(NetTblConfig.KV_TARG_YAW);
        mCurrentAngle = mTable.getEntry(NetTblConfig.KV_ROBO_YAW);
        mBlownUp = mTable.getEntry("NotBlownUp");
        mBlownUp.setBoolean(true);
    }

    public static CameraMaster getInstance() {
        if (mCameraMaster == null) {
            mCameraMaster = new CameraMaster();
        }
        return mCameraMaster;
    }

    public void addCameraData(CameraData cameraData) {
        cameraDatas.add(cameraData);
    }

    public void updateNetworkTables() {
        if (cameraDatas.size() == 2) {
            double yDispInches = getYDisplacement();
            double xDispInches = getXDisplacement();
            boolean haveTarget = isHaveTarget();
            if (abs(yDispInches) > 300){
                mBlownUp.setBoolean(false);
                haveTarget = false;
            }else{
                mBlownUp.setBoolean(true);
            }
            mYDist.setDouble(yDispInches);
            mXDist.setDouble(xDispInches);
            mHaveTarget.setBoolean(haveTarget);
        }
    }

    private boolean isHaveTarget() {
        if (cameraDatas.get(0).getHaveTarget() && cameraDatas.get(1).getHaveTarget()) {
            return true;
        } else {
            return false;
        }
    }

    private double getXDisplacement() {
        double errorAngle = mTargetAngle.getDouble(0) - mCurrentAngle.getDouble(0);
        double error = errorAngle * ERROR_CORRECTION_CONSTANT;
        mTable.getEntry("Error Angle").setDouble(errorAngle);
        mTable.getEntry("Error").setDouble(error);

        double xCam0 = cameraDatas.get(0).get_X_DIST();
        double xCam1 = cameraDatas.get(1).get_X_DIST();
        
        mTable.getEntry("Cam0 KV_X_DIST").setDouble(xCam0);
        mTable.getEntry("Cam1 KV_X_DIST").setDouble(xCam1);
        // System.out.printf("Cam.errAng : %.3f , Cam.errComp : %.3f \n", errorAngle,
        // error);
        return ((xCam0 + xCam1) / 2) - error;
    }

    private double getYDisplacement() {
        double yCam0 = cameraDatas.get(0).get_Y_DIST();
        double yCam1 = cameraDatas.get(1).get_Y_DIST();
        
        mTable.getEntry("Cam0 KV_Y_DIST").setDouble(yCam0);
        mTable.getEntry("Cam1 KV_Y_DIST").setDouble(yCam1);
        return ((yCam0 + yCam1) / 2);
    }

    private double abs(double num) {
        if (num < 0) {
            num = num * -1;
        }
        return num;
    }

}