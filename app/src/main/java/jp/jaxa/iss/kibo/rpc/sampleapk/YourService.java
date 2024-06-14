package jp.jaxa.iss.kibo.rpc.sampleapk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;

import com.google.mlkit.vision.label.ImageLabeler;

import org.opencv.core.Mat;
import org.tensorflow.lite.support.model.Model;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Logger;

import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

/**
 * Class meant to handle commands from the Ground Data System and execute them
 * in Astrobee.
 */

public class YourService extends KiboRpcService {
    int imageSize = 320;
    private ImageLabeler imageLabeler;
    @Override
    protected void runPlan1() {
        // The mission starts.
        api.startMission();
        // Move to the Target 1 position.
        movetopos(10.95, -9.9228d, 5.195, 0, 0, -0.70f, 0.70f, 3, true);
        targetack_debug(1, "item_cv", "bmptar1", "mattar1");
        // Move to the Target 2 position.
        movetopos(11.235, -9.25, 5.295, 0.707, 0.0, 0.707, 0.00, 3, true);
        movetopos(10.925, -8.875, 3.7295, 0.500, 0.500, 0.500, 0.500, 3, true);
        targetack_debug(2, "item_2", "bmptar2", "mattar2");
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
            double qua_w, int recheck, boolean log_enable) {
        final Point point = new Point(pos_x, pos_y, pos_z);
        final Quaternion quaternion = new Quaternion((float) qua_x, (float) qua_y, (float) qua_z, (float) qua_w);
        Result result = api.moveTo(point, quaternion, true);

        int loopcount = 0;
        while (result.hasSucceeded() == false) {
            if (loopcount >= recheck) {
                break;
            }
            result = api.moveTo(point, quaternion, true);
            loopcount++;
        }

        if (log_enable == true) {
            Logger logger = Logger.getLogger(YourService.class.getName());
            logger.info("Move to position: " + point.toString() + ", Quaternion: " + quaternion.toString());
        }
    }

    private void targetack_debug(int areaID, String item_name, String bitmapdebug_name, String matimgdebug_name) {
        Mat image = api.getMatNavCam();
        Bitmap bmpimage = api.getBitmapNavCam();
        api.saveBitmapImage(bmpimage, bitmapdebug_name);
        api.saveMatImage(image, matimgdebug_name);
        api.setAreaInfo(areaID, item_name);
        api.reportRoundingCompletion();
        api.notifyRecognitionItem();
        api.takeTargetItemSnapshot();

    }

    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("detect.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void classifyImage() {
        try {
            Model model = Model.newInstance(Context);

        } catch (IOException e) {
            //TODO: Handle the exception
        }

        private void imageprocessing (Intent Data){
            Bitmap image = (Bitmap) Data.getExtras().get("data");
            int dimesion = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimesion, dimesion);
            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, true);
            classifyImage(image);

        }

    }
