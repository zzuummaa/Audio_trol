package ru.zuma.video;

import static org.bytedeco.javacpp.opencv_core.*;

public interface VideoSourceInterface extends AutoCloseable {

    boolean isOpened();

    Mat grab();

    void release();

    double getFrameRate();

    @Override
    default void close() {
        release();
    }
}
