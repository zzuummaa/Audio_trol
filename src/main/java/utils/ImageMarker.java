package utils;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.Map;

import static org.opencv.core.Core.FONT_HERSHEY_PLAIN;
import static org.opencv.imgproc.Imgproc.putText;

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

    public void nameTrackedRects(Mat image, Map<Integer, Rect> rects) {
        for (Map.Entry<Integer, Rect> rectEntry: rects.entrySet()) {
            String box_text = "Number: " + rectEntry.getKey();

            Rect face_i = rectEntry.getValue();

            // Calculate the position for annotated text (make sure we don't
            // put illegal values in there):
            int pos_x = (int) Math.max(face_i.tl().x - 10, 0);
            int pos_y = (int) Math.max(face_i.tl().y - 10, 0);

            putText(image, box_text, new Point(pos_x, pos_y),
                    FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 255, 0, 2.0));
        }

    }
}
