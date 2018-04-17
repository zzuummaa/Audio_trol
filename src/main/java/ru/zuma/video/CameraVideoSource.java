package ru.zuma.video;

import org.bytedeco.javacpp.opencv_videoio.VideoCapture;

import static org.bytedeco.javacpp.opencv_core.*;

public class CameraVideoSource implements VideoSourceInterface {
    private VideoCapture capture;
    private Mat img;

    public CameraVideoSource(int index) {
        capture = new VideoCapture(index);
        img = new Mat();
    }

    public CameraVideoSource(VideoCapture capture) {
        this.capture = capture;
        this.img = new Mat();
    }

    @Override
    public boolean isOpened() {
        return capture.isOpened();
    }

    @Override
    public Mat grab() {
        capture.read(img);
        return img;
    }

    @Override
    public void release() {
        capture.release();;
    }
}
