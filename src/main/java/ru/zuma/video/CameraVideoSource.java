package ru.zuma.video;

import org.bytedeco.javacpp.opencv_videoio.VideoCapture;

import static org.bytedeco.javacpp.opencv_core.*;

public class CameraVideoSource implements VideoSourceInterface {
    private VideoCapture capture;

    public CameraVideoSource(int index) {
        capture = new VideoCapture(index);
    }

    public CameraVideoSource(VideoCapture capture) {
        this.capture = capture;
    }

    @Override
    public boolean isOpened() {
        return capture.isOpened();
    }

    @Override
    public Mat grab() {
        Mat img = new Mat();
        capture.read(img);
        return img;
    }

    @Override
    public void release() {
        capture.release();;
    }
}
