package ru.zuma.utils;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import ru.zuma.rx.RxClassifier;
import ru.zuma.rx.RxVideoSource2;
import ru.zuma.video.CameraVideoSource;
import ru.zuma.video.HttpVideoSource;
import ru.zuma.video.VideoConsumer;
import ru.zuma.video.VideoSourceInterface;

import java.io.IOException;

import static org.bytedeco.javacpp.opencv_core.Size;
import static org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;

public class ConsoleUtil {
    public static VideoConsumer createVideoConsumer(String[] args) {
        VideoConsumer consumer;
        FFmpegFrameRecorder recorder;

        System.out.println("Starting video consumer...");
        if (args.length > 1) {
            recorder = new FFmpegFrameRecorder(args[1], 640, 480, 0);
        } else {
            recorder = new FFmpegFrameRecorder("http://192.168.1.68:8090/feed.ffm", 640, 480, 0);
        }

        recorder.setOption("protocol_whitelist", "file,http,tcp");
//        recorder.setVideoBitrate(2 * 1024 * 1024);
        recorder.setFrameRate(7.5);
//        recorder.setInterleaved(true);
//        recorder.setVideoBitrate(450);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_FLV1);
//        recorder.setFormat("ffm");
//        recorder.setFrameRate(30);

        consumer = new VideoConsumer(recorder);

        System.out.println("Video consumer successfully started!");
        return consumer;
    }

    public static RxVideoSource2 createVideoSource(String[] args) {
        VideoSourceInterface videoSourceTmp;

        System.out.println("Starting video source...");

        String videoSourceURL = System.getProperty("os.name").equals("Linux") ? "/dev/video0" : null;
        String pixelFormat = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-i")) {
                videoSourceURL = i+1 < args.length ? args[i+1] : null;
                break;
            }
        }

        FrameGrabber grabber;
        if (videoSourceURL != null) {
            if (videoSourceURL.startsWith("http") | videoSourceURL.startsWith("rtsp") | videoSourceURL.startsWith("rtmp")) {
                grabber = new FFmpegFrameGrabber(videoSourceURL);
                videoSourceTmp = new HttpVideoSource(grabber);
            } else {
                grabber = new OpenCVFrameGrabber(videoSourceURL);
                grabber.setFormat("MJPEG");
                grabber.setFrameRate(15);
                videoSourceTmp = new CameraVideoSource(grabber);
            }
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

    public static RxClassifier createClassifier() throws IOException {
        CascadeClassifier diceCascade = OpenCVHelper.createFaceDetector();

        RxClassifier classifier = new RxClassifier(diceCascade);
        classifier.setDefaultMinSize(new Size(90, 90));
        classifier.setDefaultMaxSize(new Size(480, 480));
        return classifier;
        //classifier.setDefaultMinSize(new Size(0, 0));
    }

    public static String storageName(String[] args) {
        String name = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-n")) {
                name = i+1 < args.length ? args[i+1] : "Test";
            }
        }

        return name;
    }

}
