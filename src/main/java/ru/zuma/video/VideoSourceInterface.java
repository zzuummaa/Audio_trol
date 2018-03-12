package ru.zuma.video;

import static org.bytedeco.javacpp.opencv_core.*;

public interface VideoSourceInterface {

    boolean isOpened();

    Mat grab();

    void release();
}
