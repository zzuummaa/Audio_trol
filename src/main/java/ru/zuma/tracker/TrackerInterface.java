package ru.zuma.tracker;

import org.bytedeco.javacpp.opencv_core.Rect2d;

import static org.bytedeco.javacpp.opencv_core.*;

public interface TrackerInterface {
    boolean init(Mat img, Rect2d track);

    boolean update(Mat img, Rect2d track);
}
