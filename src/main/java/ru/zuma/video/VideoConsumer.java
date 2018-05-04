package ru.zuma.video;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;

import static org.bytedeco.javacpp.opencv_core.*;

public class VideoConsumer implements VideoConsumerInterface {
    private OpenCVFrameConverter.ToMat toFrame = new OpenCVFrameConverter.ToMat();
    private FFmpegFrameRecorder recorder;
    private boolean isOpened;

    public VideoConsumer(FFmpegFrameRecorder recorder) {
        this.recorder = recorder;
        try {
            recorder.start();
            isOpened = true;
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
            isOpened = false;
        }
    }

    @Override
    public boolean isOpened() {
        return isOpened;
    }

    @Override
    public boolean record(Mat mat) {
        try {
            recorder.record(toFrame.convert(mat));
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void release() {
        try {
            recorder.release();
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }
}
