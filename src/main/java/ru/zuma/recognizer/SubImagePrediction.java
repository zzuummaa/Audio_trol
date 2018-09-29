package ru.zuma.recognizer;

import static org.bytedeco.javacpp.opencv_core.*;
import ru.zuma.utils.Pair;

import java.util.ArrayList;
import java.util.Collection;

public class SubImagePrediction extends ArrayList<Pair<Rect, Prediction>> {
    public SubImagePrediction(int initialCapacity) {
        super(initialCapacity);
    }

    public SubImagePrediction() {
    }

    public SubImagePrediction(Collection<? extends Pair<Rect, Prediction>> c) {
        super(c);
    }

    public boolean add(Rect rect, Prediction prediction) {
        return add(new Pair<>(rect, prediction));
    }
}
