import junit.framework.Assert;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * Created by Fomenko_S.V. on 22.07.2017.
 */

public class SetupOpenCV extends Assert {
    @Test
    public void loadLibrary() {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Try to use VM option '-Djava.library.path=target\\natives'");
            fail();
        }
    }

    @Test
    public void simpleTestLibrary() {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            Mat mat = new Mat(10, 10, CvType.CV_64FC1);
        } catch (Throwable e) {
            e.printStackTrace();
            fail();
        }
    }

}
