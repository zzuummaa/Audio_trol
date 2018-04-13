package ru.zuma;

import java.util.ArrayDeque;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

public class AsyncClassifier extends Thread {
    interface DetectionCallback {
        void onDetections(RectVector detections);
    }

    private CascadeClassifier classifier;
    private RectVector detections;
    private ArrayDeque<Mat> imageQueue;
    private DetectionCallback detectionCallback;

    public AsyncClassifier(CascadeClassifier classifier) {
        this(classifier, true);
    }

    public AsyncClassifier(CascadeClassifier classifier, boolean isAutoStart) {
        this.classifier = classifier;
        this.detections = new RectVector();
        this.imageQueue = new ArrayDeque<Mat>(1);
        this.detectionCallback = null;
        this.setDaemon(true);

        if (isAutoStart) {
            start();
        }
    }

    public synchronized void setOnDetections(DetectionCallback detectionCallback) {
        this.detectionCallback = detectionCallback;
    }

    public RectVector getDetections() {
        return new RectVector(detections);
    }

    public void setImage(Mat image) {
        synchronized (imageQueue) {
            if (!imageQueue.isEmpty()) {
                imageQueue.clear();
            }

            imageQueue.push(image);
            imageQueue.notify();
        }
    }

    public final static Size defaultMinSize = new Size(60, 60);
    public final static Size defaultMaxSize = new Size(1800, 1800);

    public void detect(Mat image, RectVector detections) {
        detect(image, detections, defaultMinSize, defaultMaxSize);
    }

    public void detect(Mat image, RectVector detections, Size minSize, Size maxSize) {
        classifier.detectMultiScale(
                image,
                detections,
                1.1,
                3,
                0,
                minSize,
                maxSize
        ); // Performs the detection
    }

    @Override
    public void run() {
        Mat image;

        while (!isInterrupted()) {
            synchronized (imageQueue) {
                try {
                    while (imageQueue.isEmpty()) {
                        imageQueue.wait();
                    }
                } catch (InterruptedException e) {
                    break;
                }

                image = imageQueue.pop();
            }

            detect(image, detections);

            synchronized (this) {
                if (detectionCallback != null) {
                    detectionCallback.onDetections(detections);
                }
            }
        }
    }
}
