package ru.zuma.utils;

import java.util.Map;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.putText;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;

/**
 * Created by Fomenko_S.V. on 22.07.2017.
 *
 * TODO merge mark rect and text methods
 */
public class ImageMarker {
    public static void markRects(Mat image, RectVector rects) {
        // Draw a bounding box around each detection.
        Point pointLeftBottom = new Point();
        Point pointRightTop = new Point();

        Rect rect;
        for (int i = 0; i < rects.size(); i++) {
            rect = rects.get(i);

            pointLeftBottom.x(rect.x());
            pointLeftBottom.y(rect.y());

            pointRightTop.x(rect.x() + rect.width());
            pointRightTop.y(rect.y() + rect.height());

            rectangle(image, pointLeftBottom, pointRightTop, Scalar.GREEN);
        }
    }

    public static void markNamedRects(Mat image, Map<Rect, String> rects) {
        // Draw a bounding box around each detection.
        Point pointLeftBottom = new Point();
        Point pointRightTop = new Point();

        for (Map.Entry<Rect, String> rectEntry: rects.entrySet()) {
            String box_text = rectEntry.getValue();

            Rect rect = rectEntry.getKey();

            // Calculate the position for annotated text (make sure we don't
            // put illegal values in there):
            int pos_x = (int) Math.max(rect.tl().x() - 10, 0);
            int pos_y = (int) Math.max(rect.tl().y() - 10, 0);

            putText(image, box_text, new Point(pos_x, pos_y),
                    FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 255, 0, 2.0));

            pointLeftBottom.x(rect.x());
            pointLeftBottom.y(rect.y());

            pointRightTop.x(rect.x() + rect.width());
            pointRightTop.y(rect.y() + rect.height());

            rectangle(image, pointLeftBottom, pointRightTop, Scalar.GREEN);
        }
    }

    public static void markRect2d(Mat image, Rect2d rect) {
        Point pointLeftBottom = new Point();
        Point pointRightTop = new Point();

        pointLeftBottom.x((int) rect.x());
        pointLeftBottom.y((int) rect.y());

        pointRightTop.x((int) (rect.x() + rect.width()));
        pointRightTop.y((int) (rect.y() + rect.height()));

        rectangle(image, pointLeftBottom, pointRightTop, Scalar.GREEN);
    }

    public static void nameTrackedRects(Mat image, Map<Integer, Rect> rects) {
        for (Map.Entry<Integer, Rect> rectEntry: rects.entrySet()) {
            String box_text = "Number: " + rectEntry.getKey();

            Rect face_i = rectEntry.getValue();

            // Calculate the position for annotated text (make sure we don't
            // put illegal values in there):
            int pos_x = (int) Math.max(face_i.tl().x() - 10, 0);
            int pos_y = (int) Math.max(face_i.tl().y() - 10, 0);

            putText(image, box_text, new Point(pos_x, pos_y),
                    FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 255, 0, 2.0));
        }
    }
}
