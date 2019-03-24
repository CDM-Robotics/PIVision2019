
package team6072.vision;

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
    public static final String KV_X_DIST = "X_Distance";    // inches distance left (-ve)(-) or right (+ve)(+) of target center
    public static final String KV_YAW_ERR = "Yaw_Error";    // the degrees between the Robot's heading and the target
    public static final String KV_HAVE_TARGET = "HaveTarget";  // bool - true if have a target


    public static void InitTables() {
        NetworkTableInstance def = NetworkTableInstance.getDefault();
        NetworkTable visTbl = def.getTable(T_VISION);
        visTbl.getEntry(KV_HAVE_TARGET);
        visTbl.getEntry(KV_X_DIST);
        visTbl.getEntry(KV_Y_DIST);
    }
    

}
