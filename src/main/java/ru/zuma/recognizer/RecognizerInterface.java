package ru.zuma.recognizer;

import org.bytedeco.javacpp.opencv_core.*;
import java.util.List;

public interface RecognizerInterface {
    void update(MatVector images, List<Integer> labels);

    void update(Mat image, int label);

    void train(MatVector images, List<Integer> labels);

    Prediction predict(Mat image);

    SubImagePrediction predict(Mat image, RectVector subImageRects);

    void save(String fileName);

    void load(String fileName);
}
