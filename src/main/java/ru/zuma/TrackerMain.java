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

import java.awt.*;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_tracking.*;

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

    public static Rect2d requestSelectedRect(CanvasFrame canvasFrame) throws InterruptedException {
        GetRectMouseListener ml = new GetRectMouseListener();

        canvasFrame.getCanvas().addMouseListener(ml);
        synchronized (ml) {
            while (!ml.isReceivedXY.get()) ml.wait();
        }
        canvasFrame.removeMouseListener(ml);

        Rectangle r = new Rectangle(new Point(ml.x1, ml.y1));
        r.add(new Point(ml.x2, ml.y2));

        // (x1, y1) should be left bottom!
        if (ml.x1 > ml.x2) {
            int tmp = ml.x1;
            ml.x1 = ml.x2;
            ml.x2 = tmp;
        }
        if (ml.y1 > ml.y2) {
            int tmp = ml.x2;
            ml.x2 = ml.x1;
            ml.x1 = tmp;
        }

        Rect2d rect2d = new Rect2d(
                ml.x1,
                ml.y1,
                ml.x2 - ml.x1,
                ml.y2 - ml.y1);

        return rect2d;
    }

    static class GetRectMouseListener implements MouseListener {
        int x1, y1;
        int x2, y2;
        AtomicBoolean isReceivedXY = new AtomicBoolean(false);

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (isReceivedXY.get()) return;

            x1 = e.getX();
            y1 = e.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (isReceivedXY.get()) return;
            x2 = e.getX();
            y2 = e.getY();
            if (Math.abs(x1 - x2) > 40 && Math.abs(y1 - y2) > 40) {
                isReceivedXY.set(true);
                synchronized (this) {
                    this.notify();
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    };

}
