package ru.zuma.utils;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.CanvasFrame;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class CanvasFrameUtil {

    /**
     * Wait until user select rect on canvasFrame
     *
     * @param canvasFrame
     * @return selected rect
     * @throws InterruptedException if any thread interrupted the current thread
     */
    public static opencv_core.Rect2d requestSelectedRect(CanvasFrame canvasFrame) throws InterruptedException {
        GetRectMouseListener ml = new GetRectMouseListener();

        canvasFrame.getCanvas().addMouseListener(ml);
        synchronized (ml) {
            while (!ml.isReceivedXY.get()) ml.wait();
        }
        canvasFrame.removeMouseListener(ml);

        Rectangle r = new Rectangle(new Point(ml.x1, ml.y1));
        r.add(new Point(ml.x2, ml.y2));

        // (x1, y1) should be left bottom!
        if (ml.x1 > ml.x2) {
            int tmp = ml.x1;
            ml.x1 = ml.x2;
            ml.x2 = tmp;
        }
        if (ml.y1 > ml.y2) {
            int tmp = ml.x2;
            ml.x2 = ml.x1;
            ml.x1 = tmp;
        }

        opencv_core.Rect2d rect2d = new opencv_core.Rect2d(
                ml.x1,
                ml.y1,
                ml.x2 - ml.x1,
                ml.y2 - ml.y1);

        return rect2d;
    }

    static class GetRectMouseListener implements MouseListener {
        int x1, y1;
        int x2, y2;
        AtomicBoolean isReceivedXY = new AtomicBoolean(false);

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (isReceivedXY.get()) return;

            x1 = e.getX();
            y1 = e.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (isReceivedXY.get()) return;
            x2 = e.getX();
            y2 = e.getY();
            if (Math.abs(x1 - x2) > 40 && Math.abs(y1 - y2) > 40) {
                isReceivedXY.set(true);
                synchronized (this) {
                    this.notify();
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    };

}
