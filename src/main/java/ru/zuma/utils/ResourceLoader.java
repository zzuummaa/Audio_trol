package ru.zuma.utils;

import java.io.File;
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
            try {
                configureFromClass(ResourceLoader.class);
            } catch (URISyntaxException e) {
                throw new AssertionError(e);
            }
        }

        return resourceLoader;
    }

    public static void configureFromClass(Class clazz) throws URISyntaxException {
        executablePath = new File( clazz.getProtectionDomain()
                .getCodeSource().getLocation().toURI().getPath() ).getParent();

        resourcesPath = executablePath + File.separator + resourcesFolderName;

        resourceLoader = new ResourceLoader();
    }

    public String getFullPath(String resourceName) {
        return resourcesPath + File.separator + resourceName;
    }

    public String getExecutableDir() {
        return executablePath;
    }
}