import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

/**
 * Created by Fomenko_S.V. on 22.07.2017.
 */
public class Vision {
    static final String haarCascadePath = "src/main/resources/haarcascade_frontalface_alt.xml";
    private CascadeClassifier diceCascade;

    private VideoCapture capture;
    private Mat image;

    public Vision() {
        diceCascade = new CascadeClassifier(haarCascadePath);

        capture = new VideoCapture(0);

        if (!capture.isOpened()) {
            throw new RuntimeException("Can't open camera");
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

    public void realize() {
        capture.release();
    }
}
