
package team6072.vision;

import java.util.function.Consumer;

import edu.wpi.first.networktables.*;



/**
 * Sepcify the setup of the Ntwork tables shared between robo, vision, and shuffleboard
 */
public class NetTblConfig {


    // tables   ----------------------------
    
    // Used to store values written to the SmartDashboard or Shuffleboard using the
    // SmartDashboard.put() set of methods
    public static final String T_DASH = "/SmartDashBoard";

    // Used to store Test mode (Test on the Driver Station) values. Typically these
    // are Subsystems and the associated sensors and actuators.
    public static final String T_TEST = "/LiveWindow";

    // Information about the currently running match that comes from the Driver
    // Station and the Field Management System.
    public static final String T_FMS = "/FMSInfo";


    // vision table 
    public static final String T_VISION = T_DASH + "/vision";

    public static final String KV_Y_DIST = "Y_Distance";    // inches distance from target - always positive
    public static final String KV_X_DIST = "X_Distance";    // inches distance left (-ve) or right (+ve) of target center
    public static final String KV_YAW_ERR = "Yaw_Error";    // the degrees between the Robot's heading and the target
    public static final String KV_HAVE_TARGET = "HaveTarget"; // bool - true if have a target
    public static final String KV_ROBOCONTROL = "RoboControl"; // bool - true if RoboLord controlling
    public static final String KV_ROBO_YAW = "RoboYaw"; // current robot yaw heading
    public static final String KV_TARG_YAW = "RoboTargYaw";     // current target yaw
    
    private static NetworkTableInstance mDefaultTbl;

    public static void InitTables() {
        NetworkTableInstance mDefaultTbl = NetworkTableInstance.getDefault();
        mDefaultTbl.startClientTeam(6072);
        NetworkTable visTbl = mDefaultTbl.getTable(T_VISION);
        visTbl.getEntry(KV_HAVE_TARGET);
        visTbl.getEntry(KV_X_DIST);
        visTbl.getEntry(KV_Y_DIST);
        visTbl.getEntry(KV_ROBOCONTROL);
    }
    

    /**
     * Add a listener to a key. Example: 
     *      addUpdateListener(T_VISION, KV_HAVETARGET,
     *              event -> { mVisionHasTarget = event.value.getBoolean(); });
     */
    public static int addUpdateListener(String table, String key, Consumer<EntryNotification> listener) {
        NetworkTable tbl = mDefaultTbl.getTable(table);
        NetworkTableEntry entry = tbl.getEntry(key);
        return entry.addListener(listener, EntryListenerFlags.kUpdate);
    }


    public static void setVal(String table, String key, Object val) {
        NetworkTable tbl = mDefaultTbl.getTable(table);
        NetworkTableEntry entry = tbl.getEntry(key);
        entry.setValue(val);
    }

    public static double getDbl(String table, String key) {
        NetworkTable tbl = mDefaultTbl.getTable(table);
        NetworkTableEntry entry = tbl.getEntry(key);
        return entry.getDouble(0.0);
    }

    

}
