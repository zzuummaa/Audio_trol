package ru.zuma;

import io.reactivex.Observable;
import org.bytedeco.javacpp.avutil;
import ru.zuma.rx.RxClassifier;
import ru.zuma.rx.RxVideoConsumer;
import ru.zuma.rx.RxVideoSource2;
import ru.zuma.utils.ConsoleUtil;
import ru.zuma.utils.ImageMarker;

import java.util.concurrent.TimeUnit;

import static org.bytedeco.javacpp.avutil.*;
import static org.bytedeco.javacpp.avutil.av_log_set_level;
import static org.bytedeco.javacpp.opencv_core.*;

public class LiveStreamProcessing {
    public static void main(String[] args) {
        avutil.av_log_set_level(avutil.AV_LOG_INFO);

        RxVideoSource2 videoSource = ConsoleUtil.createVideoSource(args);
        RxClassifier classifier = ConsoleUtil.createClassifier();

        RxVideoConsumer consumer = new RxVideoConsumer(ConsoleUtil.createVideoConsumer(args)) {
            @Override
            public void onError(Throwable e) {
                videoSource.onComplete();
                System.exit(1);
            }
        };

        if (!consumer.isOpened()) {
            consumer.onError(null);
        }

        videoSource
                .throttleFirst(300, TimeUnit.MILLISECONDS)
                .subscribe(classifier);

        Observable<Pair<Mat, RectVector>> observable = Observable.combineLatest(
                videoSource, classifier,
                (image, detects) -> new Pair<>(image, detects)
        );

        observable.map( (pair) -> {
            ImageMarker.markRects(pair.getKey(), pair.getValue());
            return pair.getKey();
        }).subscribe(consumer);
    }
}
