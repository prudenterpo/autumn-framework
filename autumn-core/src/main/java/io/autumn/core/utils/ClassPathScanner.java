package io.autumn.core.utils;

import io.autumn.core.annotations.Component;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Scans the classpath for classes annotated with @Component.
 * Works for both file-system directories and JARs.
 *
 * Core responsibility: discover every class under a base package
 * and return only those annotated with {@link Component}.
 */
public class ClassPathScanner {

    /**
     * Scans the given base package and returns all classes annotated with @Component.
     *
     * @param basePackage the base package to scan (e.g. "com.example")
     * @return a set of component classes
     */
    public Set<Class<?>> findComponentClasses(String basePackage) {
        Set<Class<?>> components = new HashSet<>();
        String packagePath = basePackage.replace('.', '/');

        try {
            Enumeration<URL> resources = Thread.currentThread()
                    .getContextClassLoader()
                    .getResources(packagePath);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();

                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(resource.getFile(), "UTF-8");
                    scanDirectory(basePackage, filePath, components);
                } else if ("jar".equals(protocol)) {
                    scanJar(resource, packagePath, components);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan package: " + basePackage, e);
        }

        return components;
    }

    private void scanDirectory(String basePackage, String directoryPath, Set<Class<?>> classes) {
        File directory = new File(directoryPath);
        if (!directory.exists()) return;

        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(basePackage + "." + file.getName(), file.getAbsolutePath(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = file.getName().substring(0, file.getName().length() - 6);
                String fullName = basePackage + "." + className;
                tryRegisterComponent(fullName, classes);
            }
        }
    }

    private void scanJar(URL resource, String packagePath, Set<Class<?>> classes) {
        try {
            JarURLConnection connection = (JarURLConnection) resource.openConnection();
            try (JarFile jarFile = connection.getJarFile()) {
                Enumeration<JarEntry> entries = jarFile.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();

                    if (name.startsWith(packagePath) && name.endsWith(".class") && !entry.isDirectory()) {
                        String className = name.replace('/', '.').substring(0, name.length() - 6);
                        tryRegisterComponent(className, classes);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan JAR for package: " + packagePath, e);
        }
    }

    private void tryRegisterComponent(String className, Set<Class<?>> classes) {
        try {
            Class<?> clazz = Class.forName(className);
            if (clazz.isAnnotationPresent(Component.class)) {
                classes.add(clazz);
            }
        } catch (Throwable ignored) {
            // Ignore classes that fail to load (e.g., dependencies not on classpath)
        }
    }
}
