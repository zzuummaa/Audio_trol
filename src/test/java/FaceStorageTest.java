import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.zuma.utils.FaceStorage;

import java.io.IOException;

import static org.bytedeco.javacpp.opencv_core.*;

/**
 * Created by Fomenko_S.V. on 22.07.2017.
 */
public class FaceStorageTest extends Assert {
    static byte data[] = {127, 0, 0,
                            0, 127, 0,
                            0, 0, 127};

    @Before
    public void init() {
        /*try {
            OpenCVLoader.load(Main.class);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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

        Mat matRequired = new Mat(3, 3, CV_8U);
        matRequired.data().put(data, 0, 0);
        System.out.println("OpenCV Mat data:\n" + matRequired);

        assertNotNull(faceStorage.store("Test", matRequired));

        Mat[] matsReceived = faceStorage.getImages("Test");
        assertEquals(matsReceived.length, 1);

        Mat matReceived = matsReceived[0];
        assertEquals(matReceived.cols(), matRequired.cols());
        assertEquals(matReceived.rows(), matReceived.rows());

        byte[] b1 = new byte[1];
        byte[] b2 = new byte[1];
        for (int i = 0; i < matRequired.rows(); i++) {
            for (int j = 0; j < matRequired.cols(); j++) {
                matRequired.data().get(b1, i, j);
                matReceived.data().get(b2, i, j);

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

        Mat matRequired = new Mat(3, 3, CV_8U);
        matRequired.data().put(data, 0, 0);

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
