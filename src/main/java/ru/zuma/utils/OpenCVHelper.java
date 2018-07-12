package ru.zuma.utils;

import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;

public class OpenCVHelper {
    static String haarCascadeName = "haarcascade_frontalface_alt.xml";

    public static CascadeClassifier createFaceDetector() {
        return new CascadeClassifier(ResourceLoader.getInstance().getFullPath(haarCascadeName));
    }
}