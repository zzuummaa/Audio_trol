package ru.zuma.rx;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import ru.zuma.ClassifierBase;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

public class RxClassifier extends ClassifierBase implements ObservableSource<RectVector>, Observer<Mat> {
    public final Subject<RectVector> subject;

    public RxClassifier(CascadeClassifier classifier) {
        super(classifier);
        this.subject = PublishSubject.create();
    }

    public RxClassifier(CascadeClassifier classifier, Subject<RectVector> subject) {
        super(classifier);
        this.subject = subject;
    }

    @Override
    public void subscribe(Observer<? super RectVector> observer) {
        subject.subscribe();
    }

    @Override
    public void onSubscribe(Disposable d) {
        subject.onSubscribe(d);
    }

    @Override
    public void onNext(Mat img) {
        RectVector detections = new RectVector();
        detect(img, detections);
        subject.onNext(detections);
    }

    @Override
    public void onError(Throwable e) {
        subject.onError(e);
    }

    @Override
    public void onComplete() {
        subject.onComplete();
    }
}
