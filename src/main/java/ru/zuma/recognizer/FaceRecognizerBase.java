package ru.zuma.recognizer;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import ru.zuma.utils.FaceStorage;
import ru.zuma.utils.ImageProcessor;

import java.nio.IntBuffer;
import java.util.List;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_face.FisherFaceRecognizer;

public class FaceRecognizerBase implements RecognizerInterface {
    private String recognitionModelFileName = FaceStorage.STORAGE_PATH + "recognition_model.xml";
    private FaceRecognizer recognizer;
    private int imageWidth;
    private int imageHeight;

    public FaceRecognizerBase() {
        this(FisherFaceRecognizer.create());
    }

    public FaceRecognizerBase(FaceRecognizer recognizer) {
        this.recognizer = recognizer;
        this.imageWidth = 128;
        this.imageHeight = 128;
        recognizer.read(recognitionModelFileName);
    }

    @Override
    public void update(Mat image, int label) {
        MatVector images = new MatVector(1);
        images.put(0, image);

        Mat labelsMat = new Mat(1, 1, CV_32SC1);
        IntBuffer labelsBuf = labelsMat.createBuffer();
        labelsBuf.put(0, label);

        recognizer.update(images, labelsMat);
    }

    @Override
    public void update(MatVector images, List<Integer> labels) {
        Mat labelsMat = new Mat(labels.size(), 1, CV_32SC1);
        IntBuffer labelsBuf = labelsMat.createBuffer();

        for (int i = 0; i < labels.size(); i++) {
            labelsBuf.put(i, labels.get(i));
        }

        recognizer.update(images, labelsMat);
    }

    @Override
    public void train(MatVector images, List<Integer> labels) {
        Mat labelsMat = new Mat(labels.size(), 1, CV_32SC1);
        IntBuffer labelsBuf = labelsMat.createBuffer();

        for (int i = 0; i < labels.size(); i++) {
            labelsBuf.put(i, labels.get(i));
        }

        recognizer.train(images, labelsMat);
    }

    @Override
    public Prediction predict(Mat image) {
        IntPointer label = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(1);
        recognizer.predict(image, label, confidence);

        return new Prediction(label.get(0), confidence.get(0));
    }

    @Override
    public void save(String fileName) {
        recognizer.save(fileName);
    }

    @Override
    public void load(String fileName) {
        recognizer.read(fileName);
    }

    public SubImagePrediction predict(Mat image, RectVector subImageRects) {
        SubImagePrediction subImagePrediction = new SubImagePrediction();

        for (int i = 0; i < subImageRects.size(); i++) {
            Rect rect = subImageRects.get(i);

            Mat resizedSubImage = ImageProcessor.resizedSubImage(image, rect, imageWidth, imageHeight);
            Mat grayImage = ImageProcessor.toGrayScale(resizedSubImage);

            Prediction prediction = predict(grayImage);
            subImagePrediction.add(rect, prediction);

            resizedSubImage.release();
            grayImage.release();
        }

        return subImagePrediction;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }
}
