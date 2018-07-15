package ru.zuma.video;

import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import static org.bytedeco.javacpp.opencv_core.Mat;

public class CameraVideoSource implements VideoSourceInterface {
    private OpenCVFrameConverter.ToMat toMat = new OpenCVFrameConverter.ToMat();
    private OpenCVFrameGrabber grabber;
    private boolean isOpened;
    private boolean isClosed;

    public CameraVideoSource(int index) {
        grabber = new OpenCVFrameGrabber(index);
        try {
            grabber.start();
            isOpened = true;
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
            isOpened = false;
        }
    }

//    public CameraVideoSource(VideoCapture capture) {
//        this.capture = capture;
//        this.img = new Mat();
//    }

    @Override
    public boolean isOpened() {
//        return capture.isOpened();
        return isOpened;
    }

    @Override
    public Mat grab() {
        try {
            return toMat.convert(grabber.grab());
        } catch (FrameGrabber.Exception e) {
            if (!isClosed) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void release() {
        try {
            isOpened = false;
            isClosed = true;
            grabber.release();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public double getFrameRate() {
        return grabber.getFrameRate();
    }
}
