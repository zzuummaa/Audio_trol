package utils;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Fomenko_S.V. on 22.07.2017.
 */
public class ImageMarker {
    public void markRects(Mat image, MatOfRect rects) {
        // Draw a bounding box around each detection.
        for (Rect rect : rects.toArray()) {
            Imgproc.rectangle(image, new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0));
        }
    }
}
