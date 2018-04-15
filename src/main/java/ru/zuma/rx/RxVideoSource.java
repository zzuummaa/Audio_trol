package ru.zuma.rx;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import ru.zuma.video.VideoSourceInterface;

import static org.bytedeco.javacpp.opencv_core.*;

public class RxVideoSource implements VideoSourceInterface, ObservableSource<Mat>, Observer<Mat> {
    private VideoSourceInterface videoSource;
    public final Subject<Mat> subject;

    public RxVideoSource(VideoSourceInterface videoSource) {
        this.videoSource = videoSource;
        this.subject = PublishSubject.create();
    }

    public RxVideoSource(VideoSourceInterface videoSource, Subject<Mat> subject) {
        this.videoSource = videoSource;
        this.subject = subject;
    }

    @Override
    public boolean isOpened() {
        return videoSource.isOpened();
    }

    @Override
    public Mat grab() {
        return videoSource.grab();
    }

    @Override
    public void release() {
        videoSource.release();
        subject.onComplete();
    }

    @Override
    public void subscribe(Observer<? super Mat> observer) {
        subject.subscribe(observer);
    }

    @Override
    public void onSubscribe(Disposable d) {
        subject.onSubscribe(d);
    }

    @Override
    public void onNext(Mat mat) {
        subject.onNext(mat);
    }

    @Override
    public void onError(Throwable e) {
        subject.onError(e);
    }

    @Override
    public void onComplete() {
        videoSource.release();
        subject.onComplete();
    }
}
