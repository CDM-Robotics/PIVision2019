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
    private static NetworkTableEntry mTargetAngle;
    private static NetworkTableEntry mCurrentAngle;

    private final double ERROR_CORRECTION_CONSTANT = 1.2857;

    private CameraMaster() {
        cameraDatas = new ArrayList<CameraData>();
        NetworkTableInstance mNetworkInstance = NetworkTableInstance.getDefault();
        NetworkTable table = mNetworkInstance.getTable(NetTblConfig.T_VISION);
        mXDist = table.getEntry(NetTblConfig.KV_X_DIST);
        mYDist = table.getEntry(NetTblConfig.KV_Y_DIST);
        mHaveTarget = table.getEntry(NetTblConfig.KV_HAVE_TARGET);
        mTargetAngle = table.getEntry(NetTblConfig.KV_TARG_YAW);
        mCurrentAngle = table.getEntry(NetTblConfig.KV_ROBO_YAW);
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
            mYDist.setDouble(yDispInches);
            mXDist.setDouble(xDispInches);
            mHaveTarget.setBoolean(haveTarget);
        }
    }

    private boolean isHaveTarget() {
        if (cameraDatas.get(0).isHaveTarget() && cameraDatas.get(1).isHaveTarget()) {
            return true;
        } else {
            return false;
        }
    }

    private double getXDisplacement() {
        double errorAngle = mTargetAngle.getDouble(0) - mCurrentAngle.getDouble(0);
        double error = errorAngle * ERROR_CORRECTION_CONSTANT;

        double xCam1 = cameraDatas.get(0).getKV_X_DIST();
        double xCam2 = cameraDatas.get(1).getKV_X_DIST();
        // System.out.printf("Cam.errAng : %.3f , Cam.errComp : %.3f \n", errorAngle, error);
        return ((xCam1 + xCam2) / 2) - error;
    }

    private double getYDisplacement() {
        double yCam1 = cameraDatas.get(0).getKV_Y_DIST();
        double yCam2 = cameraDatas.get(1).getKV_Y_DIST();
        return ((yCam1 + yCam2) / 2);
    }

}