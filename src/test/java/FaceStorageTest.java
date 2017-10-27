import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import ru.zuma.Main;
import ru.zuma.utils.FaceStorage;
import ru.zuma.utils.OpenCVLoader;

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
            OpenCVLoader.load(Main.class);
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

    /**
     * We add one image into storage by name 'Test' and then
     * read images with name 'Test'. Expects 1 returned image
     * that has similar width, height and data matrix with original
     * image.
     *
     * @throws IOException
     */
    @Test
    public void storeAndGetImages() throws IOException {
        FaceStorage faceStorage = new FaceStorage();
        faceStorage.removeImages("Test");
        String[] fileNames = faceStorage.getFileNames("Test");
        assertEquals(fileNames == null ? 0 : fileNames.length, 0);

        Mat matRequired = new Mat(3, 3, CvType.CV_8U);
        matRequired.put(0, 0, data);
        System.out.println("OpenCV Mat data:\n" + matRequired.dump());

        assertTrue(faceStorage.store("Test", matRequired));

        Mat[] matsReceived = faceStorage.getImages("Test");
        assertEquals(matsReceived.length, 1);

        Mat matReceived = matsReceived[0];
        assertEquals(matReceived.width(), matRequired.width());
        assertEquals(matReceived.height(), matReceived.height());

        byte[] b1 = new byte[1];
        byte[] b2 = new byte[1];
        for (int i = 0; i < matRequired.height(); i++) {
            for (int j = 0; j < matRequired.width(); j++) {
                matRequired.get(i, j, b1);
                matReceived.get(i, j, b2);

                assertEquals(b1[0], b2[0]);
            }
        }

        faceStorage.removeImages("Test");
        matRequired.release();
        for (Mat mat : matsReceived) {
            mat.release();
        }
    }

    @Test
    public void deleteFileByPath() throws IOException {
        FaceStorage faceStorage = new FaceStorage();
        faceStorage.removeImages("Test");

        Mat matRequired = new Mat(3, 3, CvType.CV_8U);
        matRequired.put(0, 0, data);

        faceStorage.store("Test", matRequired);
        faceStorage.store("Test", matRequired);
        faceStorage.store("Test", matRequired);

        String[] fileNames = faceStorage.getFileNames("Test");
        assertEquals(3, fileNames == null ? 0 : fileNames.length);

        faceStorage.removeImage("Test", fileNames[0]);
        fileNames = faceStorage.getFileNames("Test");
        assertEquals(2, fileNames == null ? 0 : fileNames.length);

        faceStorage.removeImages("Test");
        fileNames = faceStorage.getFileNames("Test");
        assertEquals(0, fileNames == null ? 0 : fileNames.length);
    }
}
