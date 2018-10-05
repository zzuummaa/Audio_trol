package ru.zuma;

import io.reactivex.Observable;
import org.bytedeco.javacv.CanvasFrame;
import ru.zuma.rx.RxClassifier;
import ru.zuma.rx.RxVideoSource2;
import ru.zuma.utils.*;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import static org.bytedeco.javacpp.opencv_core.*;

public class RxClassifierMain {
    RxVideoSource2 videoSource;
    RxClassifier classifier;
    Observable<Pair<Mat, RectVector>> classifierPairObserver;
    CanvasFrame canvasFrame;
    String faceStorageName;

    public RxClassifierMain(String[] args) throws IOException {
        this.videoSource = ConsoleUtil.createVideoSource(args);
        this.classifier = ConsoleUtil.createClassifier();
        this.canvasFrame = new CanvasFrame("Reactive OpenCV sample");
        this.canvasFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.faceStorageName = ConsoleUtil.storageName(args);
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        RxClassifierMain rxClassifierMain = new RxClassifierMain(args);
        rxClassifierMain.init(true);

        rxClassifierMain.classifierPairObserver.subscribe(pair -> {
            ImageMarker.markRects(pair.first(), pair.second());
            rxClassifierMain.canvasFrame.showImage(ImageProcessor.toBufferedImage(pair.first()));
        });

        rxClassifierMain.run();
    }

    public void init(boolean isSaveFaces) {
        videoSource
            .throttleFirst(100, TimeUnit.MILLISECONDS)
            .subscribe(classifier);

        classifierPairObserver = Observable.combineLatest(
            videoSource, classifier,
            Pair::new
        );

        if (isSaveFaces) {
            AtomicReference<Optional<Point>> point = new AtomicReference<>(Optional.empty());
            canvasFrame.getCanvas().addMouseListener(new ClickMouseListener(point));
            FaceStorage faceStorage = new FaceStorage("%s%d%s");
            subscribeFaceSaver(classifierPairObserver, faceStorage, point);
        }
    }

    public void run() throws InterruptedException {

        // Idle before app exit signal
        while (canvasFrame.isShowing()) Thread.sleep(100);

        System.out.println("Realise resources...");

        synchronized (videoSource) {
            videoSource.onComplete();
        }

        System.out.println("Goodbye!");
    }

    private void subscribeFaceSaver(Observable<Pair<Mat, RectVector>> classifier, FaceStorage faceStorage, AtomicReference<Optional<Point>> point) {
        classifier.subscribe(pair -> {
            point.getAndSet(Optional.empty()).ifPresent(p -> {
                if (!pair.second().empty()) {

                    // Search for clicked rect
                    Rect r = null;
                    int i;
                    for (i = 0; i < pair.second().size(); i++) {
                        r = pair.second().get(i);
                        if (r.x() <= p.x && p.x <= r.x() + r.width()
                        &&  r.y() <= p.y && p.y <= r.y() + r.height()) {
                            break;
                        }
                    }

                    if (i == pair.second().size()) return;

                    Mat face = ImageProcessor.resizedSubImage(pair.first(), r, 128, 128);
                    String fileName = null;
                    try {
                        fileName = faceStorage.store(faceStorageName, face);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    face.release();
                    if (fileName != null) {
                        System.out.println("Photo saved as '" + fileName + "'");
                    } else {
                        System.out.println("Error when photo save");
                    }
                }
            });
        });
    }

    static class ClickMouseListener implements MouseListener {
        private AtomicReference<Optional<Point>> point;

        public ClickMouseListener(AtomicReference<Optional<Point>> point) {
            this.point = point;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            Point clickPoint = new Point(e.getX(), e.getY());
            point.set(Optional.of(clickPoint));
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
}
