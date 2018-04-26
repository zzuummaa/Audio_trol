package ru.zuma.rx;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import ru.zuma.classifier.ClassifierBase;
import ru.zuma.classifier.ClassifierInterface;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

public class RxClassifier extends Observable<RectVector> implements ClassifierInterface, ObservableSource<RectVector>, Observer<Mat> {
    private final Subject<RectVector> subject;
    private final ClassifierInterface classifier;

    public RxClassifier(CascadeClassifier classifier, Subject<RectVector> subject) {
        this.classifier = new ClassifierBase(classifier);
        this.subject = subject;
    }

    public RxClassifier(CascadeClassifier classifier) {
        this(classifier, PublishSubject.create());
    }

    public RxClassifier(ClassifierInterface classifier, Subject<RectVector> subject) {
        this.subject = subject;
        this.classifier = classifier;
    }

    public RxClassifier(ClassifierInterface classifier) {
        this(classifier, PublishSubject.create());
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

    @Override
    public void detect(Mat image, RectVector detections) {
        classifier.detect(image, detections);
    }

    @Override
    public void detect(Mat image, RectVector detections, Size minSize, Size maxSize) {
        classifier.detect(image, detections, minSize, maxSize);
    }

    @Override
    public void setDefaultMinSize(Size defaultMinSize) {
        classifier.setDefaultMinSize(defaultMinSize);
    }

    @Override
    public void setDefaultMaxSize(Size defaultMaxSize) {
        classifier.setDefaultMaxSize(defaultMaxSize);
    }

    @Override
    protected void subscribeActual(Observer<? super RectVector> observer) {
        subject.subscribe(observer);
    }
}
