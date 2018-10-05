package ru.zuma.utils;

import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;

import java.io.IOException;

// TODO merge this class with OpenCVLoader
public class OpenCVHelper {
    static String haarCascadeName = "haarcascade_frontalface_alt.xml";

    public static CascadeClassifier createFaceDetector() throws IOException {
        String path = ResourceLoader.getInstance().getFullPath(haarCascadeName);
        CascadeClassifier classifier = new CascadeClassifier(path);
        if (classifier.empty()) {
            throw new IOException("Cascade can't be loaded from " + path);
        } else {
            return classifier;
        }
    }
}