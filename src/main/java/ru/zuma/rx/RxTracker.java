package ru.zuma.rx;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.bytedeco.javacpp.opencv_core.Rect2d;
import ru.zuma.tracker.TrackerBase;
import ru.zuma.tracker.TrackerInterface;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_tracking.*;

public class RxTracker extends Observable<Rect2d> implements TrackerInterface, ObservableSource<Rect2d>, Observer<Mat> {
    private final TrackerBase trackerBase;
    private final Subject<Rect2d> subject;
    private final Rect2d track;
    private boolean isInit;

    public RxTracker() {
        this(new TrackerBase(), PublishSubject.create());
    }

    public RxTracker(Tracker tracker) {
        this(new TrackerBase(tracker), PublishSubject.create());
    }

    public RxTracker(TrackerBase trackerBase) {
        this(trackerBase, PublishSubject.create());
    }

    public RxTracker(TrackerBase trackerBase, Subject<Rect2d> subject) {
        this.trackerBase = trackerBase;
        this.subject = subject;
        this.track = new Rect2d();
    }

    @Override
    public void onSubscribe(Disposable d) {
        subject.onSubscribe(d);
    }

    @Override
    public void onNext(Mat img) {
        if (!isInit) {
            return;
        }

        trackerBase.update(img, track);
        subject.onNext(new Rect2d().put(track));
    }

    @Override
    public void onError(Throwable e) {
        subject.onError(e);
    }

    @Override
    public void onComplete() {
        subject.onComplete();
    }

    @Override
    protected void subscribeActual(Observer<? super Rect2d> observer) {
        subject.subscribe(observer);
    }

    @Override
    public boolean init(Mat img, Rect2d track) {
        this.track.put(track);
        isInit = trackerBase.init(img, this.track);
        return isInit;
    }

    @Override
    public boolean update(Mat img, Rect2d track) {
        return trackerBase.update(img, track);
    }
}
