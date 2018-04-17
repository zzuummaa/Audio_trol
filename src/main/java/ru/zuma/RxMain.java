package ru.zuma;

import io.reactivex.Observable;
import javafx.util.Pair;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacv.CanvasFrame;
import ru.zuma.rx.RxClassifier;
import ru.zuma.rx.RxVideoSource;
import ru.zuma.rx.RxVideoSource2;
import ru.zuma.utils.ImageMarker;
import ru.zuma.utils.ImageProcessor;
import ru.zuma.utils.OpenCVHelper;
import ru.zuma.video.CameraVideoSource;
import ru.zuma.video.HttpVideoSource;
import ru.zuma.video.VideoSourceInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

public class RxMain {
    RxVideoSource2 videoSource;
    RxClassifier classifier;
    CanvasFrame canvasFrame;

    public static void main(String[] args) throws InterruptedException {
        RxMain rxMain = new RxMain();

        rxMain.initVideoSource(args);
        rxMain.initClassifier();
        rxMain.canvasFrame = new CanvasFrame("Reactive OpenCV sample");
        rxMain.canvasFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        rxMain.run();
    }

    public void run() throws InterruptedException {
        AtomicReference<RectVector> detections = new AtomicReference<>(new RectVector());

        AtomicReference<Mat> img = new AtomicReference<>();

        long[] lastTime = new long[1];
        long timeout = 100;

        videoSource
                .filter((mat) -> {
                    long currTime = System.currentTimeMillis();
                    if (currTime - lastTime[0] < timeout) {
                        return false;
                    } else {
                        lastTime[0] = currTime;
                        return true;
                    }
                })
                .subscribe(classifier);

        videoSource.subscribe( (image) -> {

            if (img.get() == null) {
                img.set(image.clone());
                synchronized (img) {
                    img.notify();
                }
            }

        } );

        classifier.subject.subscribe( (detect) -> detections.set(detect) );

        Observable.combineLatest(
                videoSource, classifier.subject,
                (image, detects) -> new Pair<Mat, RectVector>(image, detects)
        ).subscribe(pair -> {
                ImageMarker.markRects(pair.getKey(), pair.getValue());
                canvasFrame.showImage(ImageProcessor.toBufferedImage(pair.getKey()));
        });

        // Idle before app exit signal
        while (canvasFrame.isShowing()) Thread.sleep(100);

        System.out.println("Realise resources...");

        synchronized (videoSource) {
            videoSource.onComplete();
        }

        System.out.println("Good bye!");
    }

    public void initClassifier() {
        CascadeClassifier diceCascade = OpenCVHelper.createFaceDetector();
        classifier = new RxClassifier(diceCascade);
        //classifier.setDefaultMinSize(new Size(0, 0));
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

        videoSource = new RxVideoSource2(videoSourceTmp);
    }
}
