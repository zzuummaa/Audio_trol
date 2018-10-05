package ru.zuma.utils;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import java.awt.image.BufferedImage;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

public class ImageProcessor {

	private static OpenCVFrameConverter.ToIplImage  iplConv = new OpenCVFrameConverter.ToIplImage();
	private static OpenCVFrameConverter.ToMat       matConv = new OpenCVFrameConverter.ToMat();
	private static Java2DFrameConverter biConv  = new Java2DFrameConverter();

	/**
	 * Clones (deep copies the data) of a {@link BufferedImage}. Necessary when
	 * converting to BufferedImages from JavaCV types to avoid re-using the same
	 * memory locations.
	 *
	 * @param source
	 * @return
	 */
	public static BufferedImage deepCopy(BufferedImage source) {
		return Java2DFrameConverter.cloneBufferedImage(source);
	}

	public synchronized static BufferedImage toBufferedImage(IplImage src) {
		return deepCopy(biConv.getBufferedImage(iplConv.convert(src)));
	}

	public synchronized static BufferedImage toBufferedImage(Mat src) {
		return deepCopy(biConv.getBufferedImage(matConv.convert(src)));
	}

	public synchronized static BufferedImage toBufferedImage(Frame src) {
		return deepCopy(biConv.getBufferedImage(src));
	}

	public synchronized static BufferedImage copyMatToBufferedImage(Mat src, BufferedImage dst) {
		biConv.copy(matConv.convert(src), dst);
		return dst;
	}

	public synchronized static BufferedImage copyFrameToBufferedImage(Frame src, BufferedImage dst) {
		biConv.copy(src, dst);
		return dst;
	}

	public synchronized static IplImage toIplImage(Mat src){
		return iplConv.convertToIplImage(matConv.convert(src));
	}

	public synchronized static IplImage toIplImage(Frame src){
		return iplConv.convertToIplImage(src);
	}

	public synchronized static IplImage toIplImage(BufferedImage src){
		return iplConv.convertToIplImage(biConv.convert(src));
	}

	public synchronized static Mat toMat(IplImage src){
		return matConv.convertToMat(iplConv.convert(src));
	}

	public synchronized static Mat toMat(Frame src){
		return matConv.convertToMat(src);
	}

	public synchronized static Mat toMat(BufferedImage src){
		return matConv.convertToMat(biConv.convert(src));
	}

	public synchronized static Mat copyFrameToMat(Frame src, Mat dst) {
		Mat srcMat = matConv.convertToMat(src);
		srcMat.copyTo(dst);
		return dst;
	}

	public synchronized static Frame toFrame(IplImage src){
		return iplConv.convert(src);
	}

	public synchronized static Frame toFrame(Mat src){
		return matConv.convert(src);
	}

	public synchronized static Frame toFrame(BufferedImage src){
		return biConv.convert(src);
	}

	public static Mat resizedSubImage(Mat mat, Rect rect, int width, int height) {
		double prop = (double) width / height;
		double notResWidth = rect.width();
		double notResHeight = rect.height();

		if (notResWidth / notResHeight > prop) {
			notResHeight = notResWidth / prop;
		} else {
			notResWidth = notResHeight * prop;
		}

		int xCenter = rect.x() + rect.width() / 2;
		int yCenter = rect.y() + rect.height() / 2;
		Rect notResRect = new Rect(
				(int)(xCenter - notResWidth / 2),
				(int)(yCenter - notResHeight / 2),
				(int)notResWidth,
				(int)notResHeight
		);

		Mat notResMat = new Mat(mat, notResRect);
		Mat resMat = new Mat();
		resize(notResMat, resMat, new Size(width, height));

		return resMat;
	}

	public static Mat toGrayScale(Mat imageRGB) {
		Mat grayImage = new Mat();
		cvtColor(imageRGB, grayImage, CV_BGR2GRAY);
		return grayImage;
	}
}
