package ru.zuma.rx;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.bytedeco.javacpp.opencv_core.Mat;
import ru.zuma.video.VideoSourceInterface;

import java.util.concurrent.atomic.AtomicBoolean;

public class RxVideoSource2 extends Observable<Mat> implements Observer<Mat> {

    public Subject<Mat> subject;
    private VideoSourceInterface videoSource;
    private Thread thread;
    private AtomicBoolean isComplete;

    public RxVideoSource2(VideoSourceInterface videoSource) {
        this.subject = PublishSubject.create();
        this.videoSource = videoSource;
        this.isComplete = new AtomicBoolean(false);

        startThreadingCapture();
    }

    private void startThreadingCapture() {
        this.thread = new Thread(new LoopCapture(),"RxVideoSource");
        thread.start();
    }

    private class LoopCapture implements Runnable {
        @Override
        public void run() {

            long lastNotNullTime = System.currentTimeMillis();
            while (!isComplete.get()) {
                Mat image = videoSource.grab();
                if (image != null) {
                    subject.onNext(image);
                    lastNotNullTime = System.currentTimeMillis();
                } else if (System.currentTimeMillis() - lastNotNullTime > 5000) {
                    System.err.println(getClass().getName() + ": frame stream is end");
                    subject.onComplete();
                    break;
                }

            }

            videoSource.release();
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
        subject.onSubscribe(d);
    }

    @Override
    public void onNext(Mat img) {
        subject.onNext(img);
    }

    @Override
    public void onError(Throwable e) {
        subject.onError(e);
    }

    @Override
    public void onComplete() {
        isComplete.set(true);
        subject.onComplete();
    }

    @Override
    protected void subscribeActual(Observer<? super Mat> observer) {
        subject.subscribe(observer);
    }

    public double getFrameRate() {
        return videoSource.getFrameRate();
    }
}
