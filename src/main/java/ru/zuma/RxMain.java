package ru.zuma;

import io.reactivex.Observable;
import javafx.util.Pair;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacv.CanvasFrame;
import ru.zuma.rx.RxClassifier;
import ru.zuma.rx.RxVideoSource2;
import ru.zuma.utils.ConsoleUtil;
import ru.zuma.utils.ImageMarker;
import ru.zuma.utils.ImageProcessor;

import java.util.concurrent.TimeUnit;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import static org.bytedeco.javacpp.opencv_core.*;

public class RxMain {
    RxVideoSource2 videoSource;
    RxClassifier classifier;
    CanvasFrame canvasFrame;

    public static void main(String[] args) throws InterruptedException {
        RxMain rxMain = new RxMain();

        rxMain.videoSource = ConsoleUtil.createVideoSource(args);
        rxMain.classifier = ConsoleUtil.createClassifier();
        rxMain.canvasFrame = new CanvasFrame("Reactive OpenCV sample");
        rxMain.canvasFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        rxMain.run();
    }

    public void run() throws InterruptedException {

        videoSource
                .throttleFirst(100, TimeUnit.MILLISECONDS)
                .subscribe(classifier);

        Observable.combineLatest(
                videoSource, classifier,
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
}
