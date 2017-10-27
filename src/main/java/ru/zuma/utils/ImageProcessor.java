package ru.zuma.utils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class ImageProcessor {

	// Convert image to Mat
	public static Mat bufferedImage2Mat(BufferedImage im) {
		// Convert INT to BYTE
		//im = new BufferedImage(im.getWidth(), im.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		// Convert bufferedimage to byte array
		byte[] pixels = ((DataBufferByte) im.getRaster().getDataBuffer())
				.getData();

		// Create a Matrix the same size of image
		Mat image = new Mat(im.getHeight(), im.getWidth(), CvType.CV_8UC3);
		// Fill Matrix with image values
		image.put(0, 0, pixels);

		return image;

	}

	public static BufferedImage toBufferedImage(Mat matrix){
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( matrix.channels() > 1 ) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = matrix.channels()*matrix.cols()*matrix.rows();
		byte [] buffer = new byte[bufferSize];
		matrix.get(0,0,buffer); // get all the pixels
		BufferedImage image = new BufferedImage(matrix.cols(),matrix.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);  
		return image;
	}

}
