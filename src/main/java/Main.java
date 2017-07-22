import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

/**
 * Created by Fomenko_S.V. on 21.07.2017.
 */
public class Main {
    static String haarCascadePath = "src/main/resources/haarcascade_frontalface_alt.xml";

    private CascadeClassifier diceCascade = new CascadeClassifier(haarCascadePath);
    private String output = "src/main/resources/out/output.png";
    private VideoCapture vc = new VideoCapture();

    private DisplayVideoFrame display = new DisplayVideoFrame();

    public static void main(String[] args) throws InterruptedException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Main main = new Main();
        main.detImg();

    }

    public void detImg() throws InterruptedException {
        vc.open(0); // Opens the video stream

        Mat image = new Mat(); // Creates an empty matrix
        while (display.isShowing()){
            if (!vc.read(image)) {
                System.out.println("Error read frame from camera");
                break;
            }

            MatOfRect diceDetections = new MatOfRect(); // Output container
            diceCascade.detectMultiScale(image, diceDetections); // Performs the detection

            // Draw a bounding box around each detection.
            for (Rect rect : diceDetections.toArray()) {
                Imgproc.rectangle(image, new Point(rect.x, rect.y),
                        new Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(0, 255, 0));
            }

            display.showImage(matToBufferedImage(image));
        }

        vc.release(); // Closes the stream.
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
