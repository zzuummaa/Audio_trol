import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import utils.ResourceLoader;

import java.util.*;

import static java.lang.Math.*;

/**
 * Created by Fomenko_S.V. on 22.07.2017.
 */
public class Vision {
    static final String haarCascadeName = "haarcascade_frontalface_alt.xml";
    private CascadeClassifier diceCascade;

    private VideoCapture capture;
    private Mat image;

    public Vision() {
        diceCascade = new CascadeClassifier(ResourceLoader.getFullPath(haarCascadeName));

        capture = new VideoCapture(0);

        if (!capture.isOpened()) {
            throw new RuntimeException("Can't open camera");
        }
    }

    public Vision(String fileName) {
        diceCascade = new CascadeClassifier(ResourceLoader.getFullPath(haarCascadeName));

        capture = new VideoCapture(fileName);

        if (!capture.isOpened()) {
            throw new RuntimeException("Can't load video file");
        }
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

    public MatOfRect detectFaces() {
        if (image != null) {
            return detectFaces(image);
        } else {
            throw new NullPointerException("First called method look()");
        }
    }

    public MatOfRect detectFaces(Mat image) {
        MatOfRect diceDetections = new MatOfRect();
        diceCascade.detectMultiScale(image, diceDetections); // Performs the detection

        return diceDetections;
    }

    private static int MAX_WIDTH_DIFF = 10;
    private static int MAX_HEIGHT_DIFF = 10;
    private static int MAX_X_DIFF = 22;
    private static int MAX_Y_DIFF = 22;
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

            int currDistance = (int) (pow(abs(rect.width  - origin.width ), 2)
                                    + pow(abs(rect.height - origin.height), 2)
                                    + pow(abs(rect.x      - origin.x     ), 2)
                                    + pow(abs(rect.y      - origin.y     ), 2));

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

    public Map<Integer, Rect> trackRects(Map<Integer, Rect> mapOfprevRects, MatOfRect currRects, Mat image) {
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
        Rect[] rectsArr = currRects.toArray();
        for (int i = 0; i < rectsArr.length; i++) {
            rects.add(rectsArr[i]);
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
    }
}
