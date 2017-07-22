import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import utils.ImageMarker;
import utils.ImageProcessor;

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
        ImageProcessor imageProcessor = new ImageProcessor();

        while (display.isShowing()){
            if (!vision.look()) {
                System.out.println("Error read frame from camera");
                break;
            }

            MatOfRect diceDetections = vision.detectFaces();

            Mat image = vision.getImage();
            marker.markRects(image, diceDetections);

            display.showImage(imageProcessor.toBufferedImage(image));
        }

        vision.realize();
        display.dispose();

    }

}
