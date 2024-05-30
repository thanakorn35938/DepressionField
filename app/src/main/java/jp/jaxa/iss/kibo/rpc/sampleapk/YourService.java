package jp.jaxa.iss.kibo.rpc.sampleapk;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import gov.nasa.arc.astrobee.types.Vec3d;

import org.opencv.core.Mat;

/**
 * Class meant to handle commands from the Ground Data System and execute them
 * in Astrobee.
 */

public class YourService extends KiboRpcService {
    @Override
    protected void runPlan1() {
        // The mission starts.
        api.startMission();
        // Move to the Target 1 position.
        movetopos(10.95, -9.9228d, 5.195, 0, 0, -0.70f, 0.70f, 3, true);
        targetack_debug(1, "item_cv", "bmptar1", "mattar1");
        // Move to the Target 2 position.
    }

    @Override
    protected void runPlan2() {
        // write your plan 2 here.
    }

    @Override
    protected void runPlan3() {
        // write your plan 3 here.
    }

    private void movetopos(double pos_x, double pos_y, double pos_z,
            double qua_x, double qua_y, double qua_z,
            double qua_w, int recheckม, boolean log_enable) {
        final int loop_max = recheck;
        final Point point = new Point(pos_x, pos_y, pos_z);
        final Quaternion quaternion = new Quaternion((float) qua_x, (float) qua_y, (float) qua_z, (float) qua_w);
        Result result = api.moveTo(point, quaternion, true);

        int loopcount = 0;
        while (((Result) result).hasSucceeded() == false) {
            if (loopcount >= loop_max) {
                break;
            }
            result = api.moveTo(point, quaternion, true);
            loopcount++;
        }

        if (log_enable == true) {
            log.logger("Flag1" + "Move to position: " + pos_x + ", " + pos_y + ", " + pos_z);
        }
    }

    private void targetack_debug(int areadID, string item_name, string bitmapdebug_name, string matimgdebug_name) {
        Mat image = getMatNavCam();
        api.saveBitmap(image, bitmapdebug_name);
        api.saveMatImage(image, matimgdebug_name);
        api.setAreaInfo(areaID, item_name);
        api.reportRoundingCompletion();
        api.notifyRecognitionItem();
        api.takeTargetItemSnapshot();
    }

    private void aruco() {

    }
}
