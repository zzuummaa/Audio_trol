import junit.framework.Assert;
import org.bytedeco.javacpp.opencv_core;
import org.junit.Test;
import ru.zuma.Main;
import ru.zuma.utils.OpenCVLoader;

import java.io.IOException;

import static org.bytedeco.javacpp.opencv_core.*;

/**
 * Created by Fomenko_S.V. on 22.07.2017.
 */

public class SetupOpenCV extends Assert {
    @Test
    public void loadLibrary() {
        try {
            OpenCVLoader.load(Main.class);
        } catch (IOException e) {
            //System.err.println("Try to use VM option '-Djava.library.path=target\\natives'");
            fail();
        }
    }

    @Test
    public void simpleTestLibrary() {
        try {
            OpenCVLoader.load(Main.class);
            Mat mat = new Mat(10, 10, CV_64FC1);
        } catch (Throwable e) {
            e.printStackTrace();
            fail();
        }
    }

}
