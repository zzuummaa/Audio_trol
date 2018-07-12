package ru.zuma.utils;

import org.bytedeco.javacpp.opencv_core;

import java.io.File;
import java.io.IOException;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_UNCHANGED;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;

/**
 * Created by Fomenko_S.V. on 22.07.2017.
 */
public class FaceStorage  {
    public static final String STORAGE_PATH = "storage/face/";
    private static final String EXTENSION = ".png";
    private static final int DIGIT_COUNT = 6;

    /**
     * Format of saving file name. Input data consists 3 arguments:
     * - simple name of directory
     * - number of data in directory
     * - extension of file
     */
    private String fileNameFormat;

    public FaceStorage() {
        this("%s%06d%s");
    }

    public FaceStorage(String fileNameFormat) {
        this.fileNameFormat = fileNameFormat;
    }

    public String store(String name, Mat image) throws IOException {
        String path = STORAGE_PATH + name;

        File directory = new File(path);

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Can't make directory '" + directory.getPath() + "'");
            }
        }

        File[] contents = directory.listFiles();

        int maxIndex = -1;
        for (File file: contents) {
            int curIndex = getNumber(file.getName());
            if (curIndex > maxIndex) {
                maxIndex = curIndex;
            }
        }

        String fullFileName = path + "/" + generateFileName(name, maxIndex+1);

        return imwrite(fullFileName, image) ? fullFileName : null;
    }

    public String[] getFileNames(String name) {
        String path = STORAGE_PATH + name;

        File directory = new File(path);
        String[] contents = directory.list();
        return contents;
    }

    public File[] getFiles(String name) {
        String path = STORAGE_PATH + name;
        return new File(path).listFiles();
    }

    public Mat[] getImages(String name, int flags) {
        String[] fileNames = getFileNames(name);
        Mat[] images = new Mat[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            images[i] = imread(STORAGE_PATH + name + "/" + fileNames[i], flags);
        }

        return images;
    }

    public Mat[] getImages(String name) {
        return getImages(name, CV_LOAD_IMAGE_UNCHANGED);
    }

    public String generateFileName(String name, int num) {
        return String.format(fileNameFormat, name, num, EXTENSION);
    }

    public int getNumber(String fileName) {
        int endIndex = fileName.length() - EXTENSION.length();
        int startIndex = endIndex;
        while (Character.isDigit(fileName.charAt(startIndex-1))) {
            startIndex--;
            if (startIndex-1 < 0) throw new NumberFormatException("Before extension should be numbers");
        }

        return Integer.parseInt(fileName.substring(startIndex, endIndex));
    }

    public void removeImages(String name) {
        deleteDirectory(new File(STORAGE_PATH + name));
    }

    public void removeImage(String name, String fileName) {
        deleteFile(new File(STORAGE_PATH + "/" + name + "/" + fileName));
    }

    public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                File f = new File(dir, children[i]);
                deleteDirectory(f);
            }
            dir.delete();
        } else dir.delete();
    }

    public static void deleteFile(File dir) {
        deleteDirectory(dir);
    }
}
