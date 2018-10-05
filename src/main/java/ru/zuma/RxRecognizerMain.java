package ru.zuma;

import ru.zuma.recognizer.FaceRecognizerBase;
import ru.zuma.recognizer.Prediction;
import ru.zuma.rx.RxRecognizer;
import ru.zuma.utils.ImageMarker;
import ru.zuma.utils.ImageProcessor;
import ru.zuma.utils.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.bytedeco.javacpp.opencv_core.Rect;
import static org.bytedeco.javacpp.opencv_face.LBPHFaceRecognizer;

public class RxRecognizerMain {
    public static void main(String[] args) throws InterruptedException, IOException {
        RxClassifierMain rxClassifierMain = new RxClassifierMain(args);
        rxClassifierMain.init(false);

        RxRecognizer rxRecognizer = new RxRecognizer(new FaceRecognizerBase(LBPHFaceRecognizer.create()));
        rxClassifierMain.classifierPairObserver.subscribe(rxRecognizer);

        rxRecognizer.subscribe(pair -> {
            Map<Rect, String> recognizedRectsMap = new HashMap<>();

            for (Pair<Rect, Prediction> rectPair : pair.second()) {
                String str = String.format("Label: %s, Confidence: %.2f", rectPair.second().getLabel(), rectPair.second().getConfidence());
                recognizedRectsMap.put(rectPair.first(), str);
            }

            ImageMarker.markNamedRects(pair.first(), recognizedRectsMap);
            rxClassifierMain.canvasFrame.showImage(ImageProcessor.toBufferedImage(pair.first()));
        });

        rxClassifierMain.run();
    }
}
