package com.arextest.web.core.business.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Created by Qzmo on 2023/8/15
 */
public class RemoteJarLoader {
    public static URLClassLoader loadJar(String jarUrl) throws MalformedURLException {
        URL resource;
        if (jarUrl.startsWith("http")) {
            resource = new URL(jarUrl);
        } else {
            resource = RemoteJarLoader.class.getClassLoader().getResource(jarUrl);
        }
        if (resource == null) {
            resource = new File(jarUrl).toURI().toURL();
        }

        return new URLClassLoader(new URL[]{resource});
    }

    public static <T> List<T> loadService(Class<T> clazz, SecureClassLoader classLoader) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz, classLoader);
        List<T> res = new ArrayList<>();
        for (T service : serviceLoader) {
            res.add(service);
        }
        return res;
    }
}
