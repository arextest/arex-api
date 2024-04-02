package com.arextest.web.core.business.beans.httpclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author jmo
 * @since 2021/9/15
 */
@Component
@Slf4j
public final class HttpWebServiceApiClient {

  private RestTemplate restTemplate;

  private RestTemplate outerRestTemplate;
  @Resource
  private ObjectMapper objectMapper;
  private int connectTimeOut = 10000;
  private int readTimeOut = 10000;

  @Autowired(required = false)
  private List<ClientHttpRequestInterceptor> clientHttpRequestInterceptors;

  @PostConstruct
  private void initTemplate() {
    initRestTemplate();
  }

  private void initRestTemplate() {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(connectTimeOut);
    requestFactory.setReadTimeout(readTimeOut);
    final int initialCapacity = 10;
    List<HttpMessageConverter<?>> httpMessageConverterList = new ArrayList<>(initialCapacity);
    httpMessageConverterList.add(new ByteArrayHttpMessageConverter());
    httpMessageConverterList.add(new StringHttpMessageConverter());
    httpMessageConverterList.add(new ResourceHttpMessageConverter());
    httpMessageConverterList.add(new SourceHttpMessageConverter<>());
    httpMessageConverterList.add(new AllEncompassingFormHttpMessageConverter());

    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(
        objectMapper);
    converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
    httpMessageConverterList.add(converter);

    // set inner restTemplate
    this.restTemplate = new RestTemplate(httpMessageConverterList);
    this.restTemplate.setRequestFactory(requestFactory);
    if (CollectionUtils.isNotEmpty(clientHttpRequestInterceptors)) {
      // Add interceptors (e.g. logging, metrics, etc
      this.restTemplate.setInterceptors(clientHttpRequestInterceptors);
    }

    // set outer restTemplate
    this.outerRestTemplate = new RestTemplate(httpMessageConverterList);
    this.outerRestTemplate.setRequestFactory(requestFactory);
  }

  public <TResponse> TResponse get(boolean inner, String url,
      Map<String, ?> urlVariables,
      Class<TResponse> responseType) {
    try {
      RestTemplate template = inner ? restTemplate : outerRestTemplate;
      return template.getForObject(url, responseType, urlVariables);
    } catch (Exception e) {
      LOGGER.error("Failed to get response from url: {}", url, e);
    }
    return null;
  }

  public <TResponse> ResponseEntity<TResponse> get(boolean inner, String url,
      Map<String, ?> urlVariables,
      ParameterizedTypeReference<TResponse> responseType) {
    try {
      RestTemplate template = inner ? restTemplate : outerRestTemplate;
      return template.exchange(url, HttpMethod.GET, null, responseType, urlVariables);
    } catch (Exception e) {
      LOGGER.error("Failed to get response from url: {}", url, e);
    }
    return null;
  }

  public <TResponse> TResponse get(boolean inner, String url,
      Map<String, ?> urlVariables,
      MultiValueMap<String, String> headers, Class<TResponse> responseType) {
    try {
      RestTemplate template = inner ? restTemplate : outerRestTemplate;
      HttpEntity<?> request = new HttpEntity<>(headers);
      return template.exchange(url, HttpMethod.GET, request, responseType, urlVariables)
          .getBody();
    } catch (Exception e) {
      LOGGER.error("Failed to get response from url: {}", url, e);
    }
    return null;
  }

  public <TResponse> TResponse get(String url,
      Map<String, ?> urlVariables,
      MultiValueMap<String, String> headers, Integer readTimeout,
      Class<TResponse> responseType) {
    try {
      SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
      requestFactory.setReadTimeout(readTimeout);
      RestTemplate template = new RestTemplate();
      template.setRequestFactory(requestFactory);
      HttpEntity<?> request = new HttpEntity<>(headers);
      return template.exchange(url, HttpMethod.GET, request, responseType, urlVariables)
          .getBody();
    } catch (Exception e) {
      LOGGER.error("Failed to get response from url: {}", url, e);
    }
    return null;
  }

  public <TRequest, TResponse> TResponse post(boolean inner, String url,
      TRequest request,
      Class<TResponse> responseType) {
    try {
      RestTemplate template = inner ? restTemplate : outerRestTemplate;
      return template.postForObject(url, wrapJsonContentType(request), responseType);
    } catch (Exception e) {
      LOGGER.error("Failed to post request to url: {}, request: {}", url, request, e);
    }
    return null;
  }

  public <TRequest, TResponse> TResponse post(boolean inner, String url,
      TRequest request,
      Class<TResponse> responseType,
      Map<String, String> headers) {
    try {
      RestTemplate template = inner ? restTemplate : outerRestTemplate;
      return template.postForObject(url, wrapJsonContentType(request, headers), responseType);
    } catch (Exception e) {
      LOGGER.error("Failed to post request to url: {}, request: {}", url, request, e);
    }
    return null;
  }


  @SuppressWarnings("unchecked")
  private <TRequest> HttpEntity<TRequest> wrapJsonContentType(TRequest request) {
    HttpEntity<TRequest> httpJsonEntity;
    if (request instanceof HttpEntity) {
      httpJsonEntity = (HttpEntity<TRequest>) request;
    } else {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      httpJsonEntity = new HttpEntity<>(request, headers);
    }
    return httpJsonEntity;
  }

  @SuppressWarnings("unchecked")
  private <TRequest> HttpEntity<TRequest> wrapJsonContentType(TRequest request,
      Map<String, String> extraHeaders) {
    HttpEntity<TRequest> httpJsonEntity;
    if (request instanceof HttpEntity) {
      httpJsonEntity = (HttpEntity<TRequest>) request;
    } else {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAll(extraHeaders);
      httpJsonEntity = new HttpEntity<>(request, headers);
    }
    return httpJsonEntity;
  }
}