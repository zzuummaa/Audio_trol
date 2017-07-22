import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import utils.ImageMarker;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

/**
 * Created by Fomenko_S.V. on 21.07.2017.
 */
public class Main {

    private DisplayVideoFrame display = new DisplayVideoFrame();

    public static void main(String[] args) throws InterruptedException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Main main = new Main();
        main.detImg();

    }

    public void detImg() throws InterruptedException {
        Vision vision = new Vision();
        ImageMarker marker = new ImageMarker();

        while (display.isShowing()){
            if (!vision.look()) {
                System.out.println("Error read frame from camera");
                break;
            }

            MatOfRect diceDetections = vision.detectFaces();

            Mat image = vision.getImage();
            marker.markRects(image, diceDetections);

            display.showImage(matToBufferedImage(image));
        }

        vision.realize();
        display.dispose();

    }

    public static BufferedImage matToBufferedImage(Mat frame) {
        //Mat() to BufferedImage
        int type = 0;
        if (frame.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (frame.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        frame.get(0, 0, data);

        return image;
    }

}
