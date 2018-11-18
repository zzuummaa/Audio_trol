package ru.zuma.utils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class ResourceLoader {
    private static ResourceLoader resourceLoader = null;

    private static String executablePath;
    private static String resourcesFolderName = "resources";
    private static String resourcesPath;

    protected ResourceLoader() {
    }

    public static ResourceLoader getInstance() {
        if (resourceLoader == null) {
            configureFromClass(ResourceLoader.class);
        }

        return resourceLoader;
    }

    public static void configureFromClass(Class clazz) {
        try {
            URI uri = clazz.getProtectionDomain().getCodeSource().getLocation().toURI();
            executablePath = new File(uri.getPath()).getParent();
            resourcesPath = executablePath + File.separator + resourcesFolderName;
            resourceLoader = new ResourceLoader();
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

    public String getFullPath(String resourceName) {
        return resourcesPath + File.separator + resourceName;
    }

    public String getExecutableDir() {
        return executablePath;
    }
}