package com.arextest.web.common;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author b_yu
 * @since 2023/2/9
 */
@Component
public class LogUtils {
    private static final String APP_TYPE = "app-type";
    private static final String AREX_WEB_API = "arex-web-api";
    private LogUtils() {

    }

    public static void init() {
        MDC.put(APP_TYPE, AREX_WEB_API);
    }

    public static void clear() {
        MDC.clear();
    }


    public static void info(Logger logger, String s) {
        info(logger, s, (Map<String, String>) null);
    }

    public static void info(Logger logger, Map<String, String> tags, String s) {
        init();
        if (MapUtils.isNotEmpty(tags)) {
            tags.forEach((k, v) -> MDC.put(k, v));
        }
        logger.info(s);
        clear();
    }

    public static void info(Logger logger, String s, Object... objects) {
        info(logger, null, s, objects);
    }

    public static void info(Logger logger, Map<String, String> tags, String s, Object... objects) {
        init();
        if (MapUtils.isNotEmpty(tags)) {
            tags.forEach((k, v) -> MDC.put(k, v));
        }
        logger.info(s, objects);
        clear();
    }



    public static void warn(Logger logger, String s) {
        warn(logger, s, (Map<String, String>) null);
    }

    public static void warn(Logger logger, String s, Map<String, String> tags) {
        init();
        if (MapUtils.isNotEmpty(tags)) {
            tags.forEach((k, v) -> MDC.put(k, v));
        }
        logger.warn(s);
        clear();
    }

    public static void warn(Logger logger, String s, Object... objects) {
        warn(logger, null, s, objects);
    }

    public static void warn(Logger logger, Map<String, String> tags, String s, Object... objects) {
        init();
        if (MapUtils.isNotEmpty(tags)) {
            tags.forEach((k, v) -> MDC.put(k, v));
        }
        logger.warn(s, objects);
        clear();
    }

    public static void warn(Logger logger, String s, Throwable throwable) {
        warn(logger, null, s, throwable);
    }

    public static void warn(Logger logger, Map<String, String> tags, String s, Throwable throwable) {
        init();
        if (MapUtils.isNotEmpty(tags)) {
            tags.forEach((k, v) -> MDC.put(k, v));
        }
        logger.warn(s, throwable);
        clear();
    }


    public static void error(Logger logger, String s) {
        error(logger, s, (Map<String, String>) null);
    }

    public static void error(Logger logger, String s, Map<String, String> tags) {
        init();
        if (MapUtils.isNotEmpty(tags)) {
            tags.forEach((k, v) -> MDC.put(k, v));
        }
        logger.error(s);
        clear();
    }

    public static void error(Logger logger, String s, Object... objects) {
        error(logger, null, s, objects);
    }

    public static void error(Logger logger, Map<String, String> tags, String s, Object... objects) {
        init();
        if (MapUtils.isNotEmpty(tags)) {
            tags.forEach((k, v) -> MDC.put(k, v));
        }
        logger.error(s, objects);
        clear();
    }

    public static void error(Logger logger, String s, Throwable throwable) {
        error(logger, null, s, throwable);
    }

    public static void error(Logger logger, Map<String, String> tags, String s, Throwable throwable) {
        init();
        if (MapUtils.isNotEmpty(tags)) {
            tags.forEach((k, v) -> MDC.put(k, v));
        }
        logger.error(s, throwable);
        clear();
    }
}
