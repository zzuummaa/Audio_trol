package ru.zuma.rx;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import ru.zuma.video.VideoConsumerInterface;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.bytedeco.javacpp.opencv_core.Mat;

public class RxAsyncVideoConsumer implements VideoConsumerInterface, Observer<Mat> {
    private BlockingQueue<Mat> qMat = new ArrayBlockingQueue<>(1);
    private VideoConsumerInterface videoConsumer;
    private Disposable disposable;
    private Thread thread;

    public RxAsyncVideoConsumer(VideoConsumerInterface videoConsumer) {
        this.videoConsumer = videoConsumer;
        this.thread = new Thread(new LoopRecord(), getClass().getSimpleName());
        thread.start();
    }

    @Override
    public synchronized void onSubscribe(Disposable d) {
        this.disposable = d;
    }

    @Override
    public void onNext(Mat mat) {
        try {
            qMat.put(mat);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isReadyForNext() {
        return qMat.isEmpty();
    }

    private class LoopRecord implements Runnable {
        @Override
        public void run() {
            try {
                while (disposable == null || !disposable.isDisposed()) {
                    Mat img = qMat.take();
                    if (!videoConsumer.record(img)) {
                        synchronized (this) {
                            if (disposable != null) disposable.dispose();
                        }
                        onError(new IOException("Can't write image"));
                        break;
                    }

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                videoConsumer.release();
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        thread.interrupt();
    }

    @Override
    public void onComplete() {
        thread.interrupt();
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
