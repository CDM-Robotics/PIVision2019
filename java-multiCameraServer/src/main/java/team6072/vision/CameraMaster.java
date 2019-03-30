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

    private CameraMaster() {
        cameraDatas = new ArrayList<CameraData>();
        NetworkTableInstance mNetworkInstance = NetworkTableInstance.getDefault();
        NetworkTable table = mNetworkInstance.getTable(NetTblConfig.T_VISION);
        mXDist = table.getEntry(NetTblConfig.KV_X_DIST);
        mYDist = table.getEntry(NetTblConfig.KV_Y_DIST);
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
        if(cameraDatas.size() == 2){
            double yDispInches = getYDisplacement();
            double xDispInches = getXDisplacement();
            mYDist.setDouble(yDispInches);
            mXDist.setDouble(xDispInches);
        }
    }

    private double getXDisplacement() {
        double xCam1 = cameraDatas.get(0).getKV_X_DIST();
        double xCam2 = cameraDatas.get(1).getKV_X_DIST();
        return ((xCam1 + xCam2) / 2);
    }

    private double getYDisplacement() {
        double yCam1 = cameraDatas.get(0).getKV_Y_DIST();
        double yCam2 = cameraDatas.get(1).getKV_Y_DIST();
        return ((yCam1 + yCam2) / 2);
    }

}