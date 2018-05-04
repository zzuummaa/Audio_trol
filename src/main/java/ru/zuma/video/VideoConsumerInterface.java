package ru.zuma.video;

import org.bytedeco.javacpp.opencv_core;

import static org.bytedeco.javacpp.opencv_core.*;

public interface VideoConsumerInterface extends AutoCloseable {
    boolean isOpened();

    boolean record(Mat mat);

    void release();

    @Override
    default void close() {
        release();
    }
}
