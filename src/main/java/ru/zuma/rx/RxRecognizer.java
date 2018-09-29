package ru.zuma.rx;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import ru.zuma.recognizer.FaceRecognizerBase;
import ru.zuma.recognizer.Prediction;
import ru.zuma.recognizer.RecognizerInterface;
import ru.zuma.recognizer.SubImagePrediction;
import ru.zuma.utils.Pair;

import java.util.List;

import static org.bytedeco.javacpp.opencv_core.*;

public class RxRecognizer extends Observable<Pair<Mat, SubImagePrediction>> implements RecognizerInterface, ObservableSource<Pair<Mat, SubImagePrediction>>, Observer<Pair<Mat, RectVector>> {
    private final RecognizerInterface recognizer;
    private final Subject<Pair<Mat, SubImagePrediction>> subject;

    public RxRecognizer(RecognizerInterface recognizer, Subject<Pair<Mat, SubImagePrediction>> subject) {
        this.subject = subject;
        this.recognizer = recognizer;
    }

    public RxRecognizer(RecognizerInterface recognizer) {
        this(recognizer, PublishSubject.create());
    }

    public RxRecognizer() {
        this(new FaceRecognizerBase(), PublishSubject.create());
    }

    @Override
    protected void subscribeActual(Observer<? super Pair<Mat, SubImagePrediction>> observer) {
        subject.subscribe(observer);
    }

    @Override
    public void onSubscribe(Disposable d) {
        subject.onSubscribe(d);
    }

    @Override
    public void onNext(Pair<Mat, RectVector> pair) {
        SubImagePrediction subImagePrediction = recognizer.predict(pair.first(), pair.second());
        subject.onNext(new Pair<>(pair.first(), subImagePrediction));
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
    public void update(MatVector images, List<Integer> labels) {
        recognizer.update(images, labels);
    }

    @Override
    public void update(Mat image, int label) {
        recognizer.update(image, label);
    }

    @Override
    public void train(MatVector images, List<Integer> labels) {
        recognizer.train(images, labels);
    }

    @Override
    public Prediction predict(Mat image) {
        return recognizer.predict(image);
    }

    @Override
    public SubImagePrediction predict(Mat image, RectVector subImageRects) {
        return recognizer.predict(image, subImageRects);
    }

    @Override
    public void save(String fileName) {
        recognizer.save(fileName);
    }

    @Override
    public void load(String fileName) {
        recognizer.load(fileName);
    }
}
