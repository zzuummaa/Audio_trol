package ru.zuma;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

public class ClassifierBase {
    private CascadeClassifier classifier;
    private Size defaultMinSize = new Size(60, 60);
    private Size defaultMaxSize = new Size(1800, 1800);

    public ClassifierBase(CascadeClassifier classifier) {
        this.classifier = classifier;
    }

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

    public void setDefaultMinSize(Size defaultMinSize) {
        this.defaultMinSize = defaultMinSize;
    }

    public void setDefaultMaxSize(Size defaultMaxSize) {
        this.defaultMaxSize = defaultMaxSize;
    }
}
