package ru.zuma.utils;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import ru.zuma.rx.RxClassifier;
import ru.zuma.rx.RxVideoSource2;
import ru.zuma.video.*;

public class ConsoleUtil {
    public static VideoConsumer createVideoConsumer(String[] args) {
        VideoConsumer consumer;
        FFmpegFrameRecorder recorder;

        System.out.println("Starting video consumer...");
        if (args.length > 1) {
            recorder = new FFmpegFrameRecorder(args[2], 640, 480, 0);
        } else {
            recorder = new FFmpegFrameRecorder("http://192.168.1.68:8090/feed.ffm", 640, 480, 0);
        }

        recorder.setInterleaved(true);
        recorder.setVideoBitrate(200);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFormat("ffm");
        recorder.setFrameRate(30);

        consumer = new VideoConsumer(recorder);

        return consumer;
    }

    public static RxVideoSource2 createVideoSource(String[] args) {
        VideoSourceInterface videoSourceTmp;

        System.out.println("Starting video source...");
        if (args.length > 0) {
            videoSourceTmp = new HttpVideoSource(args[0]);
        } else {
            videoSourceTmp = new CameraVideoSource(0);
        }

        if (!videoSourceTmp.isOpened()) {
            System.err.println("Video source not open");
            videoSourceTmp.release();
            System.exit(-1);
        }
        System.out.println("Video source successfully started!");

        return new RxVideoSource2(videoSourceTmp);
    }

    public static RxClassifier createClassifier() {
        opencv_objdetect.CascadeClassifier diceCascade = OpenCVHelper.createFaceDetector();
        return new RxClassifier(diceCascade);
        //classifier.setDefaultMinSize(new Size(0, 0));
    }
}
