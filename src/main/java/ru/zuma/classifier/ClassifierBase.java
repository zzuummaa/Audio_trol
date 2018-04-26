package ru.zuma.classifier;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

public class ClassifierBase implements ClassifierInterface {
    private CascadeClassifier classifier;
    private Size defaultMinSize = new Size(60, 60);
    private Size defaultMaxSize = new Size(1800, 1800);

    public ClassifierBase(CascadeClassifier classifier) {
        this.classifier = classifier;
    }

    @Override
    public void detect(Mat image, RectVector detections) {
        detect(image, detections, defaultMinSize, defaultMaxSize);
    }

    @Override
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
    public void setDefaultMinSize(Size defaultMinSize) {
        this.defaultMinSize = defaultMinSize;
    }

    @Override
    public void setDefaultMaxSize(Size defaultMaxSize) {
        this.defaultMaxSize = defaultMaxSize;
    }
}
