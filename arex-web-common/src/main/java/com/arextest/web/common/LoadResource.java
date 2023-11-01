package com.arextest.web.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoadResource {

  @Resource
  private ResourceLoader resourceLoader;

  public String getResource(String resourceName) {
    try {
      InputStream is = resourceLoader.getResource(resourceName).getInputStream();
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      StringBuilder sb = new StringBuilder(1000);
      String s = null;
      while ((s = br.readLine()) != null) {
        sb.append(s);
      }
      return sb.toString();
    } catch (Exception e) {
      LogUtils.error(LOGGER, String.format("Failed to get resource. resourceName:%s", resourceName),
          e);
    }
    return null;
  }
}
