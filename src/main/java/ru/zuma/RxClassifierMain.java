package ru.zuma;

import io.reactivex.Observable;
import javafx.util.Pair;

import org.bytedeco.javacv.CanvasFrame;
import ru.zuma.rx.RxClassifier;
import ru.zuma.rx.RxVideoSource2;
import ru.zuma.utils.ConsoleUtil;
import ru.zuma.utils.FaceStorage;
import ru.zuma.utils.ImageMarker;
import ru.zuma.utils.ImageProcessor;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

public class RxClassifierMain {
    RxVideoSource2 videoSource;
    RxClassifier classifier;
    CanvasFrame canvasFrame;
    String faceStorageName;

    public static void main(String[] args) throws InterruptedException {
        RxClassifierMain rxClassifierMain = new RxClassifierMain();

        rxClassifierMain.videoSource = ConsoleUtil.createVideoSource(args);
        rxClassifierMain.classifier = ConsoleUtil.createClassifier();
        rxClassifierMain.canvasFrame = new CanvasFrame("Reactive OpenCV sample");
        rxClassifierMain.canvasFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        rxClassifierMain.faceStorageName = ConsoleUtil.storageName(args);
        rxClassifierMain.run();
    }

    public void run() throws InterruptedException {

        videoSource
                .throttleFirst(100, TimeUnit.MILLISECONDS)
                .subscribe(classifier);

        Observable<Pair<Mat, RectVector>> observable = Observable.combineLatest(
                videoSource, classifier,
                (image, detects) -> new Pair<Mat, RectVector>(image, detects)
        );
        observable.subscribe(pair -> {
            ImageMarker.markRects(pair.getKey(), pair.getValue());
            canvasFrame.showImage(ImageProcessor.toBufferedImage(pair.getKey()));
        });

        AtomicBoolean isClicked = new AtomicBoolean(false);
        canvasFrame.getCanvas().addMouseListener(new ClickMouseListener(isClicked));
        FaceStorage faceStorage = new FaceStorage("%s%d%s");
        subscribeFaceSaver(observable, faceStorage, isClicked);

        // Idle before app exit signal
        while (canvasFrame.isShowing()) Thread.sleep(100);

        System.out.println("Realise resources...");

        synchronized (videoSource) {
            videoSource.onComplete();
        }

        System.out.println("Good bye!");
    }

    private void subscribeFaceSaver(Observable<Pair<Mat, RectVector>> classifier, FaceStorage faceStorage, AtomicBoolean isSaving) {
        classifier.subscribe(pair -> {
            if (isSaving.get()) {
                if (!pair.getValue().empty()) {
                    Mat face = matFromCenter(pair.getKey(), pair.getValue().get(0), 128, 128);
                    String fileName = faceStorage.store(faceStorageName, face);
                    face.release();
                    if (fileName != null) {
                        System.out.println("Photo saved as '" + fileName + "'");
                    } else {
                        System.out.println("Error when photo save");
                    }
                }
                isSaving.set(false);
            }
        });
    }

    private Mat matFromCenter(Mat mat, Rect rect, int width, int height) {
        double prop = (double) width / height;
        double notResWidth = rect.width();
        double notResHeight = rect.height();

        if ((double) notResWidth / notResHeight > prop) {
            notResHeight = notResWidth / prop;
        } else {
            notResWidth = notResHeight * prop;
        }

        int xCenter = (int) (rect.x() + rect.width() / 2);
        int yCenter = (int) (rect.y() + rect.height() / 2);
        Rect notResRect = new Rect(
                (int)(xCenter - notResWidth / 2),
                (int)(yCenter - notResHeight / 2),
                (int)notResWidth,
                (int)notResHeight
        );

        Mat notResMat = new Mat(mat, notResRect);
        Mat resMat = new Mat();
        resize(notResMat, resMat, new Size(width, height));

        return resMat;
    }

    static class ClickMouseListener implements MouseListener {
        private AtomicBoolean isClicked;

        public ClickMouseListener(AtomicBoolean isClicked) {
            this.isClicked = isClicked;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            isClicked.set(true);
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
