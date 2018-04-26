package ru.zuma.tracker;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_tracking.*;

public class TrackerBase implements TrackerInterface {
    private final Tracker tracker;

    public TrackerBase() {
        this.tracker = TrackerTLD.create();
    }

    public TrackerBase(Tracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public boolean init(Mat img, Rect2d track) {
        return tracker.init(img, track);
    }

    @Override
    public boolean update(Mat img, Rect2d track) {
        return tracker.update(img, track);
    }
}
