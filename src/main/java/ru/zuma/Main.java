package ru.zuma;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.CanvasFrame;
import ru.zuma.utils.ImageMarker;
import ru.zuma.utils.ImageProcessor;
import ru.zuma.utils.OpenCVLoader;

import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.bytedeco.javacpp.opencv_core.*;

/**
 * Created by Fomenko_S.V. on 21.07.2017.
 */
public class Main {

    private CanvasFrame display;

    public static void main(String[] args) throws InterruptedException, IOException {
        //OpenCVLoader.load(Main.class);

        Main main = new Main();
        main.display = new CanvasFrame("Video frame");

        Thread.sleep(100);
        main.detImg();

    }

    public void detImg() throws InterruptedException {
        Vision vision = new Vision(/*"C:\\Users\\Stephan\\Downloads\\БУХАЮЩИЕ БАБЫ.mp4"*/);
        ImageMarker marker = new ImageMarker();
        ImageProcessor imageProcessor = new ImageProcessor();

        Map<Integer, Rect> prevRects = new HashMap<Integer, Rect>();
        Map<Integer, Rect> currRects = null;

        while (display.isShowing()){
            if (!vision.look()) {
                System.out.println("Error read frame from camera");
                break;
            }

            RectVector diceDetections = vision.detectFaces();

            Mat image = vision.getImage();
            marker.markRects(image, diceDetections);

            currRects = vision.trackRects(prevRects, diceDetections, image);
            marker.nameTrackedRects(image, currRects);
            prevRects = currRects;

            display.showImage(imageProcessor.toBufferedImage(image));
        }

        vision.realize();
        display.dispose();

    }

}
