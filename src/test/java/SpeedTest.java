import org.junit.Before;

import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.junit.Test;
import ru.zuma.classifier.AsyncClassifier;
import ru.zuma.utils.ResourceLoader;
import ru.zuma.video.CameraVideoSource;
import ru.zuma.video.VideoSourceInterface;

import java.io.IOException;

import static org.bytedeco.javacpp.opencv_core.*;

public class SpeedTest {
    private static final String haarCascadeName = "haarcascade_frontalface_alt.xml";
    private AsyncClassifier asyncClassifier;
    private Mat[] img;

    @Before
    public void init() throws IOException {
        CascadeClassifier diceCascade = new CascadeClassifier(ResourceLoader.getInstance().getFullPath(haarCascadeName));
        asyncClassifier = new AsyncClassifier(diceCascade);


        try (VideoSourceInterface videoSource = new CameraVideoSource(0)) {

            if (!videoSource.isOpened()) {
                videoSource.release();
                throw new IOException("Can't open camera");
            }

            img = new Mat[20];

            System.out.println("Grab " + img.length + " images from camera...");

            long startTime = System.currentTimeMillis();
            for (int i = 0; i < img.length; i++) {
                img[i] = videoSource.grab();
            }
            long endTime = System.currentTimeMillis();

            System.out.println("Wasted " + (endTime - startTime) + " ms.");
            System.out.println("Time per frame: " + (endTime - startTime)/img.length + " ms");
        }
    }

    @Test
    public void speedTest() {
        RectVector detections = new RectVector();

        System.out.println();
        System.out.println("Detecting faces...");

        Size minSize = new Size(0, 0);

        for (int i = 5; i < 120; i += 10) {
            minSize.width(i);
            minSize.height(i);

            long startTime = System.currentTimeMillis();
            for (int j = 0; j < img.length; j++) {
                asyncClassifier.detect(img[j], detections, minSize, AsyncClassifier.defaultMaxSize);
            }
            long endTime = System.currentTimeMillis();

            System.out.printf("Size(%d, %d) Time per frame: %d ms",
                    minSize.width(), minSize.height(), (endTime - startTime)/img.length);
            System.out.println();

        }

    }

}