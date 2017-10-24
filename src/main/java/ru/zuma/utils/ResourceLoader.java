package ru.zuma.utils;

import java.io.File;
import java.net.URISyntaxException;

public class ResourceLoader {
    private static String executablePath;
    private static String resourcesFolderName = "resources";
    private static String resourcesPath;
    static {
        try {
            executablePath = new File( ResourceLoader.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI().getPath() ).getParent();

            resourcesPath = executablePath + "\\" + resourcesFolderName;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static String getFullPath(String resourceName) {
        return resourcesPath + "\\" + resourceName;
    }

    public static String getExecutableDir() {
        return executablePath;
    }
}