package ru.zuma;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import javafx.util.Pair;
import org.bytedeco.javacv.CanvasFrame;
import ru.zuma.rx.RxTracker;
import ru.zuma.rx.RxVideoSource2;
import ru.zuma.utils.ConsoleUtil;
import ru.zuma.utils.ImageMarker;
import ru.zuma.utils.ImageProcessor;

import java.io.IOException;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_tracking.*;
import static ru.zuma.utils.CanvasFrameUtil.requestSelectedRect;

public class TrackerMain {

    public static void main(String[] args) throws InterruptedException, IOException {
        CanvasFrame canvasFrame = new CanvasFrame("Reactive OpenCV sample");
        canvasFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        RxVideoSource2 videoSource = ConsoleUtil.createVideoSource(args);

        Mat[] frame = new Mat[] {new Mat()};
        Disposable disposable = videoSource.subscribe((img) -> {
            canvasFrame.showImage(ImageProcessor.toBufferedImage(img));
            synchronized (frame) {
                frame[0].release();
                frame[0] = img.clone();
            }
        });

        Rect2d initBbox = requestSelectedRect(canvasFrame);
        disposable.dispose();

        RxTracker tracker = new RxTracker(TrackerTLD.create());
        synchronized (frame) {
            tracker.init(frame[0], initBbox);
        }
        videoSource.subscribe(tracker);

        Observable.combineLatest(
                videoSource, tracker,
                (image, track) -> new Pair<Mat, Rect2d>(image, track)
        ).subscribe(pair -> {
            ImageMarker.markRect2d(pair.getKey(), pair.getValue());
            canvasFrame.showImage(ImageProcessor.toBufferedImage(pair.getKey()));
        });

        // Idle before app exit signal
        while (canvasFrame.isShowing()) {
            Thread.sleep(100);
        }

        System.out.println("Realise resources...");

        synchronized (videoSource) {
            videoSource.onComplete();
        }

        System.out.println("Good bye!");
    }

}
