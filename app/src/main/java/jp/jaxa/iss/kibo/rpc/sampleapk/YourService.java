package jp.jaxa.iss.kibo.rpc.sampleapk;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

import javax.naming.spi.DirStateFactory.Result;

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
        movetopos(10.95, -10.58, 5.195, 1, 0, 0, 0, 3);

        Mat image = api.getMatNavCam();
        api.setAreaInfo(1, "item_name", 1);
        api.notifyRecognitionItem();
        api.takeTargetItemSnapshot();
    }

    @Override
    protected void runPlan2() {
        // write your plan 2 here.
    }

    @Override
    protected void runPlan3() {
        // write your plan 3 here.
    }

    private void movetopos(double pos_x, doubley pos_y, double pos_z,
            double quat_x, double quat_y, double quat_z,
            double quat_w, float recheck) {
        final int loop_max = recheck;
        final Point point = new Point(pos_x, pos_y, pos_z);
        final Quaternion quaternion = new Quaternion((float) qua_x, (float) qua_y, (float) qua_z, (float) qua_w);
        Result result = api.moveTo(point, quaternion, true);

        int loopcount = 0;
        while (result.hasSucceeded() == false) {
            if (loopcount >= loop_max) {
                break;
            }
            result = api.moveTo(point, quaternion, true);
            loopcount++;
        }
    }
}
