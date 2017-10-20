import junit.framework.Assert;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import utils.OpenCVLoader;

import java.io.IOException;

/**
 * Created by Fomenko_S.V. on 22.07.2017.
 */

public class SetupOpenCV extends Assert {
    @Test
    public void loadLibrary() {
        try {
            OpenCVLoader.load();
        } catch (IOException e) {
            //System.err.println("Try to use VM option '-Djava.library.path=target\\natives'");
            fail();
        }
    }

    @Test
    public void simpleTestLibrary() {
        try {
            OpenCVLoader.load();
            Mat mat = new Mat(10, 10, CvType.CV_64FC1);
        } catch (Throwable e) {
            e.printStackTrace();
            fail();
        }
    }

}
