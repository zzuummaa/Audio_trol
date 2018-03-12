package ru.zuma;

import org.bytedeco.javacv.CanvasFrame;
import ru.zuma.utils.ImageMarker;
import ru.zuma.utils.ImageProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.bytedeco.javacpp.opencv_core.*;

/**
 * Created by Fomenko_S.V. on 21.07.2017.
 */
public class Main {

    private CanvasFrame display;
    private Vision vision;

    public static void main(String[] args) throws InterruptedException, IOException {
        //OpenCVLoader.load(Main.class);

        Main main = new Main();

        System.out.println("Starting vision...");
        if (args.length > 0) {
            main.vision = new Vision(args[0]);
        } else {
            main.vision = new Vision();
        }
        System.out.println("Vision successfully started!");

        main.display = new CanvasFrame("Video frame");

        Thread.sleep(100);
        main.detImg();

    }

    public void detImg() throws InterruptedException {
        ImageMarker marker = new ImageMarker();
        ImageProcessor imageProcessor = new ImageProcessor();

        Map<Integer, Rect> prevRects = new HashMap<Integer, Rect>();
        Map<Integer, Rect> currRects = null;

        final RectVector[] diceDetections = new RectVector[1];
        diceDetections[0] = new RectVector();
        AsyncClassifier classifier = vision.getClassifier();
        classifier.setOnDetections( (RectVector detections) -> diceDetections[0] = detections );

        while (display.isShowing()){
            if (!vision.look()) {
                System.out.println("Error read frame from camera");
                break;
            }

            vision.detectFaces();

            Mat image = vision.getImage();
            marker.markRects(image, diceDetections[0]);

            currRects = vision.trackRects(prevRects, diceDetections[0], image);
            marker.nameTrackedRects(image, currRects);
            prevRects = currRects;

            display.showImage(imageProcessor.toBufferedImage(image));
        }

        vision.realize();
        display.dispose();
        System.out.println("Good by!");

    }

}
