package ru.zuma.rx;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import ru.zuma.video.VideoConsumerInterface;

import static org.bytedeco.javacpp.opencv_core.*;

public class RxVideoConsumer implements VideoConsumerInterface, Observer<Mat> {
    private VideoConsumerInterface videoConsumer;
    private Observer<Mat> observer;
    private Disposable disposable;

    public RxVideoConsumer(VideoConsumerInterface videoConsumer) {
        this.videoConsumer = videoConsumer;
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
    }

    @Override
    public void onNext(Mat mat) {
        if (!videoConsumer.record(mat)) {
            disposable.dispose();
            onError(null);
        }
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

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
