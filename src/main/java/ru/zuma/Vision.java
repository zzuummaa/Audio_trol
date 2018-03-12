package ru.zuma;

import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacpp.opencv_videoio;
import ru.zuma.utils.ResourceLoader;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;
import static org.bytedeco.javacpp.opencv_videoio.*;
import java.util.*;

import static java.lang.Math.*;

/**
 * Created by Fomenko_S.V. on 22.07.2017.
 */
public class Vision {
    static final String haarCascadeName = "haarcascade_frontalface_alt.xml";
    private AsyncClassifier classifier;

    private opencv_videoio.VideoCapture capture;
    private Mat image;

    public Vision() {
        CascadeClassifier diceCascade = new CascadeClassifier(ResourceLoader.getInstance().getFullPath(haarCascadeName));
        classifier = new AsyncClassifier(diceCascade);

        capture = new opencv_videoio.VideoCapture(0);

        if (!capture.isOpened()) {
            throw new RuntimeException("Can't open camera");
        }
    }

    public Vision(String fileName) {
        CascadeClassifier diceCascade = new CascadeClassifier(ResourceLoader.getInstance().getFullPath(haarCascadeName));
        classifier = new AsyncClassifier(diceCascade);

        capture = new VideoCapture(fileName);

        if (!capture.isOpened()) {
            throw new RuntimeException("Can't load video file");
        }
    }

    public AsyncClassifier getClassifier() {
        return classifier;
    }

    public Mat getImage() {
        return image;
    }

    public boolean look() {
        if (image == null) {
            image = new Mat();
        }
        return capture.read(image);
    }

    public RectVector detectFaces() {
        if (image != null) {
            return detectFaces(image);
        } else {
            throw new NullPointerException("First called method look()");
        }
    }

    public RectVector detectFaces(Mat image) {
        classifier.setImage(image);
        RectVector diceDetections = classifier.getDetections();

        return diceDetections;
    }

    private static int MAX_WIDTH_DIFF = 15;
    private static int MAX_HEIGHT_DIFF = 15;
    private static int MAX_X_DIFF = 32;
    private static int MAX_Y_DIFF = 32;
    private static int MAX_SQR_DISTANCE = (int) (pow(MAX_WIDTH_DIFF, 2) + pow(MAX_HEIGHT_DIFF, 2)
                                               + pow(MAX_X_DIFF, 2    ) + pow(MAX_Y_DIFF, 2     ));

    private Rect findRect(Rect origin, List<Rect> rects) {
        if (rects.size() < 1) {
            return null;
        }

        Rect nearest = null;
        int distance = Integer.MAX_VALUE;

        int i = 0;
        Iterator<Rect> iterator = rects.iterator();
        while (iterator.hasNext()){
            Rect rect = iterator.next();

            int currDistance = (int) (pow(Math.abs(rect.width()  - origin.width() ), 2)
                                    + pow(Math.abs(rect.height() - origin.height()), 2)
                                    + pow(Math.abs(rect.x()      - origin.x()     ), 2)
                                    + pow(Math.abs(rect.y()      - origin.y()     ), 2));

            if (distance > currDistance) {
                nearest = rect;
                distance = currDistance;
            }
        }

        if (distance <= MAX_SQR_DISTANCE) {
            return nearest;
        } else {
            return null;
        }
    }

    public Map<Integer, Rect> trackRects(Map<Integer, Rect> mapOfprevRects, RectVector currRects, Mat image) {
        Map<Integer, Rect> mapOfCurrRects = new HashMap<Integer, Rect>();

        List<Rect> prevRects = new LinkedList<Rect>();
        int nextIndex = 0;
        for (Map.Entry<Integer, Rect> rectEntry: mapOfprevRects.entrySet()) {
            prevRects.add(rectEntry.getValue());

            if (nextIndex < rectEntry.getKey()) {
                nextIndex = rectEntry.getKey();
            }
        }

        nextIndex++;

        List<Rect> rects = new LinkedList<Rect>();
        for (int i = 0; i < currRects.size(); i++) {
            rects.add(currRects.get(i));
        }

        Iterator<Map.Entry<Integer, Rect>> iterator = mapOfprevRects.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Rect> prevEntry = iterator.next();
            Rect currRect = findRect(prevEntry.getValue(), rects);

            if (currRect != null) {
                mapOfCurrRects.put(prevEntry.getKey(), currRect);
                rects.remove(currRect);
            }
        }

        for (Rect rect: rects) {
            mapOfCurrRects.put(nextIndex++, rect);
        }

        return mapOfCurrRects;
    }

    public void realize() {
        capture.release();
        classifier.interrupt();
    }
}
