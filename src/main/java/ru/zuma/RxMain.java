package ru.zuma;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.CanvasFrame;
import ru.zuma.rx.RxClassifier;
import ru.zuma.rx.RxVideoSource;
import ru.zuma.utils.ImageMarker;
import ru.zuma.utils.ImageProcessor;
import ru.zuma.utils.OpenCVHelper;
import ru.zuma.video.CameraVideoSource;
import ru.zuma.video.HttpVideoSource;
import ru.zuma.video.VideoSourceInterface;

import java.util.concurrent.atomic.AtomicReference;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

public class RxMain {
    RxVideoSource videoSource;
    RxClassifier classifier;
    CanvasFrame canvasFrame;

    public static void main(String[] args) {
        RxMain rxMain = new RxMain();

        rxMain.initVideoSource(args);
        rxMain.initClassifier();
        rxMain.canvasFrame = new CanvasFrame("Reactive OpenCV sample");
        rxMain.run();
    }

    public void run() {
        canvasFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        AtomicReference<RectVector> detections = new AtomicReference<>();
        AtomicReference<Mat> img = new AtomicReference<>();

        videoSource.subscribe(classifier);
        videoSource.subject.subscribe( (image) -> img.set(image) );

        classifier.subject.subscribe( (detect) -> detections.set(detect) );

        Thread thread = new Thread(() -> {
            while (true) {
                Mat tmpImg;

                synchronized (videoSource) {

                    if (videoSource.subject.hasComplete()) {
                        break;
                    }

                    tmpImg = videoSource.grab();

                }

                videoSource.onNext(tmpImg);
            }

        });
        thread.start();

        RectVector currDetections = null;
        Mat currImage = null;

        while (canvasFrame.isShowing()) {
            RectVector localDetections = detections.get();
            Mat localImage = img.get();

            if (currDetections != localDetections ||
                currImage != localImage) {

                currDetections = localDetections;
                currImage = localImage;

                Mat markedImg = currImage.clone();
                ImageMarker.markRects(markedImg, currDetections);

                canvasFrame.showImage(ImageProcessor.toBufferedImage(markedImg));
            }

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }

        System.out.println("Realise resources...");

        synchronized (videoSource) {
            videoSource.onComplete();
        }

        System.out.println("Good bye!");
    }

    public void initClassifier() {
        CascadeClassifier diceCascade = OpenCVHelper.createFaceDetector();
        classifier = new RxClassifier(diceCascade);
    }

    public void initVideoSource(String[] args) {
        VideoSourceInterface videoSourceTmp;

        System.out.println("Starting video source...");
        if (args.length > 0) {
            videoSourceTmp = new HttpVideoSource(args[0]);
        } else {
            videoSourceTmp = new CameraVideoSource(0);
        }

        if (!videoSourceTmp.isOpened()) {
            System.err.println("Video source not open");
            videoSourceTmp.release();
            System.exit(-1);
        }
        System.out.println("Video source successfully started!");

        videoSource = new RxVideoSource(videoSourceTmp);
    }
}
