import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import utils.FaceStorage;
import utils.OpenCVLoader;

import java.io.IOException;

/**
 * Created by Fomenko_S.V. on 22.07.2017.
 */
public class FaceStorageTest extends Assert {
    static double data[] = {255, 0, 0,
                            0, 255, 0,
                            0, 0, 255};

    @Before
    public void init() {
        try {
            OpenCVLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getNumber() {
        FaceStorage faceStorage = new FaceStorage();

        String fileName = "Hren000011.jpg";
        int number = faceStorage.getNumber(fileName);
        assertEquals("numbers", 11, number);

        fileName = "Hren100001.jpg";
        number = faceStorage.getNumber(fileName);
        assertEquals("numbers", 100001, number);

        fileName = "Hren010010.jpg";
        number = faceStorage.getNumber(fileName);
        assertEquals("numbers", 10010, number);
    }

    @Test
    public void store() throws IOException {
        FaceStorage faceStorage = new FaceStorage();

        Mat mat = new Mat(3, 3, CvType.CV_8U);

        mat.put(0, 0, data);

        System.out.println("OpenCV Mat data:\n" + mat.dump());

        if ( !faceStorage.store("Test", mat) ) {
            System.out.println("Can't store mat");
            fail();
        }
    }

    @Test
    public void loadImages() {
        FaceStorage faceStorage = new FaceStorage();

        Mat[] images = faceStorage.getImages("Test");
        System.out.println("Images loaded: " + images.length);
        System.out.println();
        System.out.println("Images[0] data:\n" + images[0].dump());
        System.out.println();

        double[] actualData = new double[images[0].height() * images[0].width()];
        for (int i = 0; i < images[0].rows(); i++) {
            for (int j = 0; j < images[0].cols(); j++) {
                actualData[i*3 + j] = images[0].get(i, j)[0];
            }
        }

        System.out.println("Actual data length: " + actualData.length);

        assertArrayEquals(data, actualData, 0.001);
    }
}
