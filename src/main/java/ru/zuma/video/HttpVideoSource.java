package ru.zuma.video;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;

import static org.bytedeco.javacpp.opencv_core.*;

public class HttpVideoSource implements VideoSourceInterface {
    OpenCVFrameConverter.ToMat toMat = new OpenCVFrameConverter.ToMat();
    private FFmpegFrameGrabber grabber;
    private boolean isOpened;

    public HttpVideoSource(String url) {
        this(new FFmpegFrameGrabber(url));

    }

    public HttpVideoSource(FFmpegFrameGrabber grabber) {
        this.grabber = grabber;
        try {
            grabber.start();
            isOpened = true;
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
            isOpened = false;
        }
    }

    @Override
    public boolean isOpened() {
        return isOpened;
    }

    @Override
    public Mat grab() {
        try {
            return toMat.convert(grabber.grab());
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void release() {
        try {
            grabber.release();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }
}
