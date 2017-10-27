package ru.zuma.utils;

import org.opencv.core.Core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;

public class OpenCVLoader {
    private static boolean isLoaded;

    public static void load(Class clazz) throws IOException {
        String executableDirName = null;
        try {

            executableDirName = new File( clazz.getProtectionDomain()
                    .getCodeSource().getLocation().toURI().getPath() ).getParent();

        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }

        try {
            addDir(executableDirName + "/natives");
        } catch (IOException e) {
        }

        /*try {
            addDir(executableDirName + "/target/natives");
        } catch (IOException e) {
        }*/

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.loadLibrary("opencv_ffmpeg300_64");
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
