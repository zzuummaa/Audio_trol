package utils;

import org.opencv.core.Core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class OpenCVLoader {
    private static boolean isLoaded;

    public static void load() throws IOException {
        String currDirName = new File( "." ).getCanonicalPath();

        try {
            addDir(currDirName + "/natives");
        } catch (IOException e) {
        }

        try {
            addDir(currDirName + "/target/natives");
        } catch (IOException e) {
        }

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void addDir(String s) throws IOException {
        try {
            // This enables the java.library.path to be modified at runtime
            // From a Sun engineer at http://forums.sun.com/thread.jspa?threadID=707176
            //
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = (String[])field.get(null);
            for (int i = 0; i < paths.length; i++) {
                if (s.equals(paths[i])) {
                    return;
                }
            }
            String[] tmp = new String[paths.length+1];
            System.arraycopy(paths,0,tmp,0,paths.length);
            tmp[paths.length] = s;
            field.set(null,tmp);
            System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + s);
        } catch (IllegalAccessException e) {
            throw new IOException("Failed to get permissions to set library path");
        } catch (NoSuchFieldException e) {
            throw new IOException("Failed to get field handle to set library path");
        }
    }

    public static boolean isLoaded() {
        return isLoaded;
    }
}