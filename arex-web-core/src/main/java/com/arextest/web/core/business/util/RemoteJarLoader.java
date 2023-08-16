package com.arextest.web.core.business.util;

import com.arextest.desensitization.extension.DataDesensitization;

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
    public static SecureClassLoader loadJar(String jarUrl) throws MalformedURLException {
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

    public static void main(String[] args) throws MalformedURLException {
        SecureClassLoader classLoader = loadJar("http://maven.release.ctripcorp.com/nexus/content/repositories/flightsnapshot/com/arextest/arex-desensitization-core/0.0.1-SNAPSHOT/arex-desensitization-core-0.0.1-20230815.073853-2.jar");
        List<DataDesensitization> service = loadService(DataDesensitization.class, classLoader);
        System.out.println(service);
    }
}
