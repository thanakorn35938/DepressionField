package jp.jaxa.iss.kibo.rpc.sampleapk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import org.opencv.core.Mat;
import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Logger;
import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

/**
 * Class meant to handle commands from the Ground Data System and execute them
 * in Astrobee.f
 * 
 */
public class YourService extends KiboRpcService {
    int IMAGE_SIZE = 320;

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

    private void TFL() {
        // Load the TFLite model
        String modelPath = "detect.tflite";
        Interpreter.Options options = new Interpreter.Options();
        Interpreter interpreter = new Interpreter(new File(modelPath), options);

        // Prepare the input bitmap
        Bitmap inputBitmap = api.getBitmapNavCam();
        Bitmap opt_bitmap;
        opt_bitmap = inputBitmap.createScaledBitmap(inputBitmap, IMAGE_SIZE, IMAGE_SIZE, false);

        // TODO : @SCYT - Bitmap Scaling
        /**
         * Preprocess the input bitmap
         * // TODO: Implement any necessary preprocessing steps
         * // Convert the input bitmap to grayscale
         * Bitmap grayscaleBitmap = toGrayscale(opt_bitmap);
         * // Normalize the pixel values
         * Bitmap normalizedBitmap = normalize(grayscaleBitmap);
         * // Resize the bitmap to the desired input size
         * Bitmap resizedBitmap = resize(normalizedBitmap, IMAGE_SIZE, IMAGE_SIZE);
         * // Convert the resized bitmap to a ByteBuffer
         * ByteBuffer inputBuffer = convertToByteBuffer(resizedBitmap);
         * 
         * // Define the preprocessing methods
         * private Bitmap toGrayscale(Bitmap bitmap) {
         * Bitmap grayscaleBitmap = Bitmap.createBitmap(bitmap.getWidth(),
         * bitmap.getHeight(), Bitmap.Config.ARGB_8888);
         * Canvas canvas = new Canvas(grayscaleBitmap);
         * ColorMatrix colorMatrix = new ColorMatrix();
         * colorMatrix.setSaturation(0);
         * Paint paint = new Paint();
         * ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
         * paint.setColorFilter(filter);
         * canvas.drawBitmap(bitmap, 0, 0, paint);
         * return grayscaleBitmap;
         * }
         * 
         * private Bitmap normalize(Bitmap bitmap) {
         * Bitmap normalizedBitmap = Bitmap.createBitmap(bitmap.getWidth(),
         * bitmap.getHeight(), Bitmap.Config.ARGB_8888);
         * int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
         * bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
         * bitmap.getHeight());
         * int sum = 0;
         * for (int pixel : pixels) {
         * sum += Color.red(pixel);
         * }
         * int mean = sum / (bitmap.getWidth() * bitmap.getHeight());
         * for (int i = 0; i < pixels.length; i++) {
         * int normalizedPixel = (Color.red(pixels[i]) - mean) / 255;
         * pixels[i] = Color.rgb(normalizedPixel, normalizedPixel, normalizedPixel);
         * }
         * normalizedBitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0,
         * bitmap.getWidth(), bitmap.getHeight());
         * return normalizedBitmap;
         * }
         * 
         * private Bitmap resize(Bitmap bitmap, int width, int height) {
         * return Bitmap.createScaledBitmap(bitmap, width, height, false);
         * }
         * 
         * private ByteBuffer convertToByteBuffer(Bitmap bitmap) {
         * ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bitmap.getWidth() *
         * bitmap.getHeight() * 4);
         * byteBuffer.order(ByteOrder.nativeOrder());
         * int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
         * bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
         * bitmap.getHeight());
         * for (int pixel : pixels) {
         * byteBuffer.putFloat(Color.red(pixel) / 255f);
         * }
         * return byteBuffer;
         * }
         **/

        // Convert the input bitmap to a ByteBuffer

        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(320 * 320 * 4);
        inputBuffer.order(ByteOrder.nativeOrder());
        inputBuffer.rewind();
        inputBitmap.copyPixelsToBuffer(inputBuffer);

        // Run inference on the input
        // TODO: Implement the inference logic using the TFLite interpreter

        // Postprocess the output
        // TODO: Implement any necessary postprocessing steps

        // Clean up resources
        interpreter.close();
    }

    private void movetopos(double pos_x, double pos_y, double pos_z,
            double qua_x, double qua_y, double qua_z,
            double qua_w, int recheck, boolean log_enable) {
        final Point point = new Point(pos_x, pos_y, pos_z);
        final Quaternion quaternion = new Quaternion((float) qua_x, (float) qua_y, (float) qua_z, (float) qua_w);
        Result result = api.moveTo(point, quaternion, true);

        int loopcount = 0;
        while (!result.hasSucceeded() && loopcount < recheck) {
            result = api.moveTo(point, quaternion, true);
            loopcount++;
        }

        if (log_enable) {
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

}
