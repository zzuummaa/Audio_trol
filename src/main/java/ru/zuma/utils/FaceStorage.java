package ru.zuma.utils;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.IOException;

/**
 * Created by Fomenko_S.V. on 22.07.2017.
 */
public class FaceStorage  {
    public static final String STORAGE_PATH = "storage/face/";
    private static final String EXTENSION = ".png";
    private static final int DIGIT_COUNT = 6;

    public FaceStorage() {
    }

    public boolean store(String name, Mat image) throws IOException {
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
        return Imgcodecs.imwrite(fullFileName, image);
    }

    public String[] getFileNames(String name) {
        String path = STORAGE_PATH + name;

        File directory = new File(path);
        String[] contents = directory.list();
        return contents;
    }

    public Mat[] getImages(String name) {
        String[] fileNames = getFileNames(name);

        Mat[] images = new Mat[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            images[i] = Imgcodecs.imread(STORAGE_PATH + name + "/" + fileNames[i], Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
        }

        return images;
    }

    public String generateFileName(String name, int num) {
        return String.format("%s%06d%s", name, num, EXTENSION);
    }

    public int getNumber(String fileName) {
        int startIndex = fileName.length() - (DIGIT_COUNT + EXTENSION.length());
        int endIndex = startIndex + DIGIT_COUNT;
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
