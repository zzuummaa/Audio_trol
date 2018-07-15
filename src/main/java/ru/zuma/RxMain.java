package ru.zuma;

import org.bytedeco.javacv.CanvasFrame;
import ru.zuma.rx.RxVideoSource2;
import ru.zuma.utils.ConsoleUtil;
import ru.zuma.utils.ImageProcessor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class RxMain {
    RxVideoSource2 videoSource;
    CanvasFrame canvasFrame;

    public static void main(String[] args) throws InterruptedException, IOException {
        RxMain rxMain = new RxMain();

        rxMain.videoSource = ConsoleUtil.createVideoSource(args);
        rxMain.canvasFrame = new CanvasFrame("Reactive OpenCV sample");
        rxMain.canvasFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        rxMain.run();
    }

    public void run() throws InterruptedException, IOException {
        long startTime = System.currentTimeMillis();
        long endTime;
        AtomicInteger frameCount = new AtomicInteger(0);

        videoSource
                .throttleFirst(100, TimeUnit.MILLISECONDS)
                .subscribe( img -> {
                    canvasFrame.showImage(ImageProcessor.toBufferedImage(img));
                    frameCount.incrementAndGet();
                } );

        // Idle before app exit signal
        while (canvasFrame.isShowing()) {
            endTime = System.currentTimeMillis();

            if (endTime - startTime > 500) {
                System.out.println("fps: " + 1000 * (float)frameCount.getAndSet(0) / (endTime - startTime));
                startTime = endTime;
            }

            Thread.sleep(100);
        }

        System.out.println("Realise resources...");

        synchronized (videoSource) {
            videoSource.onComplete();
        }

        System.out.println("Good bye!");
    }
}
