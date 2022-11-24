package com.arextest.web.common;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Slf4j
public class HttpUtils {
    private HttpUtils() {
    }

    public static <T, R> ResponseEntity<T> post(String url, R request, Class<T> responseClazz) {
        return call(url, request, responseClazz, HttpMethod.POST, null, null, null);
    }

    public static <T, R> ResponseEntity<T> post(String url, R request, Class<T> responseClazz, String contentType,
                                                Map<String, String> headers, Integer timeout) {
        return call(url, request, responseClazz, HttpMethod.POST, contentType, headers, timeout);
    }


    public static <T> ResponseEntity<T> get(String url, Class<T> responseClazz) {
        return call(url, null, responseClazz, HttpMethod.GET, null, null, null);
    }

    public static <T> ResponseEntity<T> get(String url, Class<T> responseClazz, String contentType,
                                            Map<String, String> headers, Integer timeout) {
        return call(url, null, responseClazz, HttpMethod.GET, contentType, headers, timeout);
    }

    
    public static <T, R> ResponseEntity<T> call(String url, R request, Class<T> responseClazz, HttpMethod httpMethod,
                                                String contentType, Map<String, String> headers, Integer timeout) {
        ResponseEntity<T> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        if (Strings.isBlank(url)) {
            return responseEntity;
        }
        try {
            RestTemplate restTemplate = new RestTemplate();
            if (timeout != null && timeout > 0) {
                HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
                httpRequestFactory.setReadTimeout(timeout);
                restTemplate = new RestTemplate(httpRequestFactory);
            }
            HttpHeaders httpHeaders = new HttpHeaders();
            if (headers != null) {
                httpHeaders.setAll(headers);
            }
            if (!Strings.isBlank(contentType)) {
                httpHeaders.setContentType(MediaType.parseMediaType(contentType));
            } else if (httpHeaders.getContentType() == null) {
                httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
            }
            HttpEntity<R> requestEntity = new HttpEntity<>(request, httpHeaders);

            responseEntity = restTemplate.exchange(url, httpMethod, requestEntity, responseClazz);
        } catch (Exception e) {
            LOGGER.error(String.format("[HttpUtils error] url:%1$s, request:%2$s, exception:%3$s",
                    url, new Gson().toJson(request), e.toString()));
            responseEntity = new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
        return responseEntity;
    }
}
