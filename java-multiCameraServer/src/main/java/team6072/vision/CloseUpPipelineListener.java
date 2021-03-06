package team6072.vision;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.*;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.*;
import org.opencv.core.MatOfPoint2f;

import org.opencv.imgproc.Imgproc;
import team6072.vision.NetTblConfig;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.ArrayList;
import org.opencv.core.Mat;

import java.util.List;
import java.util.SortedMap;

import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;


public class CloseUpPipelineListener implements VisionRunner.Listener<CloseUpPipeline> {

    // The object to synchronize on to make sure the vision thread doesn't
    // write to variables the main thread is using.
    private final Object visionLock = new Object();

    // Network Table Entrys
    private NetworkTable mTbl;

    private final double DIST_BETWEEN_TAPE_INCHES = (5.65572 * 2);
    private final double TAPE_HEIGHT_IN_INCHES = 5.825352102;
    private final double CAMERA_FOV_ANGLE_X = 0.8859899107;
    private final double CAMERA_FOV_ANGLE_Y = 1.028149899; // fix this bc its probably wrong
    private final int CAMERA_PIXEL_WIDTH = 160;
    private final int CAMERA_PIXEL_HEIGHT_PIXELS = 120;

    // Pipelines
    private CloseUpPipeline pipeline;

    // Camera Servers
    private CameraServer mCameraServer;
    private CvSource mHVSThresholdOutput;
    private CvSource mRectanglesOutput;

    // CameraData
    private CameraData mCameraData;

    // Counters
    private int mCallCounter = 0;
    private int mCounter;

    private String mId;

    public CloseUpPipelineListener(String id) {
        mId = id;
        // instantiate CameraData shell
        mCameraData = new CameraData();
        // instantiate Network Tables
        NetworkTableInstance tblInst = NetworkTableInstance.getDefault();
        mTbl = tblInst.getTable(NetTblConfig.T_VISION);
        // Instantiate Camera Server Stuff
        mCameraServer = CameraServer.getInstance();
        HSVThresholdOutput mHsvThresOut = HSVThresholdOutput.getInstance();
        System.out.println(mHsvThresOut.getHSVName());
        // output HSVThreshold output to the camera server on Shuffleboard
        mHVSThresholdOutput = mCameraServer.putVideo(mHsvThresOut.getHSVName(), 320, 240);
    }

    /**
     * Called when the pipeline has run. We need to grab the output from the
     * pipeline then communicate to the rest of the system over network tables
     */
    @Override
    public void copyPipelineOutputs(CloseUpPipeline pipeline) {
        synchronized (visionLock) {
            // HSV output
            outputColorFilters(pipeline);

            ArrayList<MatOfPoint> mats = pipeline.findContoursOutput();
            ArrayList<RotatedRect> rotatedRects = new ArrayList<RotatedRect>();

            // Send real information
            if (mats.size() != -1) {
                for (int i = 0; i < mats.size(); i++) {
                    RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(mats.get(i).toArray()));
                    rotatedRects.add(rect);
                }

                if (rotatedRects != null && rotatedRects.size() >= 2) {
                    // orders the rectangles
                    // rotatedRects = orderFilterRectangles(rotatedRects);

                    double xDistFromCenter = getDistFromCenterInches(rotatedRects);
                    double yDistInches = getDistanceFromTargetInches(rotatedRects);
                    // Updates CameraData
                    mCameraData.updateData(xDistFromCenter, yDistInches, true);
                } else {
                    mCameraData.setHaveTarget(false);
                }
            }
            // Timestamper.getInstance().recordTime2();
            //System.out.printf("TotalPiRtime : %d \n", Timestamper.getInstance().getRunTime());
        }
    }


    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------
    // -------------------------- Unique Camera
    // outputs--------------------------------------
    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------

    public void outputColorFilters(CloseUpPipeline pipeline) {
        Mat erodeOutput = pipeline.hsvThresholdOutput();
        mHVSThresholdOutput.putFrame(erodeOutput);
    }

    public void outputRectangles(Mat mat) {
        mRectanglesOutput.putFrame(mat);
    }

    private boolean m_inCopyPipeline = false;

    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------
    // ----------------------------------Ordering the filtered in
    // Rectangles------------------
    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------

    /**
     * Returns the same array list of 2 RotatedRectangles but ordered from left to
     * right - this is so that subsequent calculations so longer have to worry about
     * the ordering of the rectangles
     * 
     * @param rotatedRects - an array list of the RotatedRectangles directly from
     *                     the camera's contour output
     * @return - an array list the two largest rectangles in list sorted by size
     */
    private ArrayList<RotatedRect> orderFilterRectangles(ArrayList<RotatedRect> rotatedRects) {
        if (rotatedRects.size() >= 2) {
            ArrayList<RotatedRect> tempRotatedRects = new ArrayList<RotatedRect>();

            RotatedRect rectBig1 = rotatedRects.get(0);
            RotatedRect rectBig2 = rotatedRects.get(1);
            for (int i = 0; i < rotatedRects.size(); i++) {
                RotatedRect tempRect = rotatedRects.get(i);
                if (tempRect.size.area() != rectBig1.size.area()){
                    if (tempRect.size.area() > rectBig1.size.area()) {
                        // rectangle is larger than Big1
                        rectBig2 = rectBig1;        // move Big1 to Big2
                        rectBig1 = tempRect;        // move current to Big1
                    } else if (tempRect.size.area() > rectBig2.size.area()) {
                        rectBig2 = tempRect;
                    }
                }
            }
            tempRotatedRects.add(rectBig1);
            tempRotatedRects.add(rectBig2);
            return tempRotatedRects;
        }
        return null;
    }

    public double abs(double num) {
        if (num < 0) {
            num = num * -1;
        }
        return num;
    }

    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------
    // ----------------------------------Finding the Distance from Target (Y)
    // ----------------
    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------

    /**
     * gives you the center of mass of the x Axis in pixel length
     * 
     * @param rotatedRects
     * @return
     */
    // private double getCenterOfMassX(ArrayList<RotatedRect> rotatedRects) {
    //     double mass1 = rotatedRects.get(0).size.height * rotatedRects.get(0).size.width;
    //     double mass2 = rotatedRects.get(1).size.height * rotatedRects.get(1).size.width;

    //     double massCenterXpx = centerOfMass(mass1, mass2, rotatedRects.get(0).center.x, rotatedRects.get(1).center.x);
    //     return massCenterXpx;
    // }

    // /**
    //  * Calculates the center of mass between two objects of varying pixel mass
    //  */
    // private double centerOfMass(double size1, double size2, double position1, double position2) {
    //     return (((size1 * position1) + (size2 * position2)) / (size1 + size2));
    // }


    private double mLastRatio = -1;

    /**
     * returns the ratio of (inches / pixels) based off the of rectangle targets
     * 
     * @param rotatedRects
     * @return
     */
    private double getRatioPxToInches(ArrayList<RotatedRect> rotatedRects) {

        double center0 = rotatedRects.get(0).center.x;
        double center1 = rotatedRects.get(1).center.x;
        double distInPx = abs(center0 - center1);
        mTbl.getEntry(mId + ": Dist IN Px").setDouble(distInPx);
        mTbl.getEntry(mId + ": center0").setDouble(center0);
        mTbl.getEntry(mId + ": center1").setDouble(center1);

        double ratio = DIST_BETWEEN_TAPE_INCHES / distInPx;
        return ratio;
    }


    /**
     * This function finds the distance between the camera and the target, -
     * assuming that the target is perfectly perpendicular to the target
     * horizantally - uses the horizantal axis ONLY, meaning it does not take into
     * account vertical distortion
     * 
     * @param rotatedRects - the Array list of ordered Rotated Rectanlges
     * @return - returns the distance inbetween the camera and the target in inches
     */
    private double getDistanceFromTargetInches(ArrayList<RotatedRect> rotatedRects) {

        // calculate the ratio from pixels to inches
        double ratio = getRatioPxToInches(rotatedRects);
        mTbl.getEntry(mId + "Vision Ratio").setDouble(ratio);
        // Calculate the distance from the target X horizontally
        double distanceFromTargetInches = (ratio * (CAMERA_PIXEL_WIDTH / 2))
                / (java.lang.Math.tan(CAMERA_FOV_ANGLE_X / 2));

        return distanceFromTargetInches;
    }

    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------
    // ----------------------------------Finding the Distance from Target
    // (X)-----------------
    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------

    private double getDistFromCenterInches(ArrayList<RotatedRect> rotatedRects) {
        int center = CAMERA_PIXEL_WIDTH / 2;
        int targetCenter = (int) ((rotatedRects.get(0).center.x + rotatedRects.get(1).center.x) / 2);
        int distplacementX = center - targetCenter;
        double ratio = getRatioPxToInches(rotatedRects);
        return (distplacementX * ratio);
    }

}