package ru.zuma.utils;

import org.bytedeco.javacpp.opencv_objdetect;
import ru.zuma.rx.RxClassifier;
import ru.zuma.rx.RxVideoSource2;
import ru.zuma.video.CameraVideoSource;
import ru.zuma.video.HttpVideoSource;
import ru.zuma.video.VideoSourceInterface;

public class ConsoleUtil {
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
