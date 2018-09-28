package ru.zuma.classifier;

import static org.bytedeco.javacpp.opencv_core.*;

public interface ClassifierInterface {

    void detect(Mat image, RectVector detections);

    void detect(Mat image, RectVector detections, Size minSize, Size maxSize);

    void setDefaultMinSize(Size defaultMinSize);

    void setDefaultMaxSize(Size defaultMaxSize);
}
