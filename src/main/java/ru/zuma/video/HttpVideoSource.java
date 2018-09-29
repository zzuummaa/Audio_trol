package ru.zuma.video;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;

import static org.bytedeco.javacpp.opencv_core.*;

public class HttpVideoSource implements VideoSourceInterface {
    private OpenCVFrameConverter.ToMat toMat = new OpenCVFrameConverter.ToMat();
    private FFmpegFrameGrabber grabber;
    private boolean isOpened;

    public HttpVideoSource(String url) {
        this(new FFmpegFrameGrabber(url));
    }

    public HttpVideoSource(String url, int timeout) {
        this(new FFmpegFrameGrabber(url));
        System.err.println(getClass().getName() + " warning: constructor parameter 'timeout' is not used");
    }

    public HttpVideoSource(FFmpegFrameGrabber grabber) {
        this.grabber = grabber;
        try {
            // For rtsp waiting forever situation
            grabber.setOption("stimeout" , "10000000");
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

    @Override
    public double getFrameRate() {
        return grabber.getFrameRate();
    }
}
