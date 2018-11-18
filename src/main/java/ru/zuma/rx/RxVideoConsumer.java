package ru.zuma.rx;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import ru.zuma.video.VideoConsumerInterface;

import java.io.IOException;

import static org.bytedeco.javacpp.opencv_core.*;

public class RxVideoConsumer implements VideoConsumerInterface, Observer<Mat> {
    private VideoConsumerInterface videoConsumer;
    private Disposable disposable;

    public RxVideoConsumer(VideoConsumerInterface videoConsumer) {
        this.videoConsumer = videoConsumer;
    }

    @Override
    public synchronized void onSubscribe(Disposable d) {
        this.disposable = d;
    }

    @Override
    public void onNext(Mat mat) {
        if (!videoConsumer.record(mat)) {
            synchronized (this) {
                if (disposable != null) disposable.dispose();
            }
            onError(new IOException("Can't write image"));
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        release();
    }

    @Override
    public void onComplete() {
        release();
    }

    @Override
    public boolean isOpened() {
        return videoConsumer.isOpened();
    }

    @Override
    public boolean record(Mat mat) {
        return videoConsumer.record(mat);
    }

    @Override
    public void release() {
        videoConsumer.release();
    }
}
