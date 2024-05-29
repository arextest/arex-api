package com.arextest.web.common;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

/**
 * Created by rchen9 on 2023/3/16.
 */
@Slf4j
public class ZstdUtils {

  private static final int ONE_K_BUFFER_SIZE = 1024;

  private static final String PATTERN_STRING = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$";
  private static final Pattern BASE_64_PATTERN = Pattern.compile(PATTERN_STRING);

  public static String base64Decode(String requestMessage) {
    if (Strings.isEmpty(requestMessage)) {
      return requestMessage;
    }
    if (BASE_64_PATTERN.matcher(requestMessage).matches()) {
      return new String(Base64.getDecoder().decode(requestMessage));
    }
    return requestMessage;
  }

  public static byte[] compress(byte[] bytes) {
    if (bytes == null) {
      return new byte[0];
    }

    try {
      return Zstd.compress(bytes, 3);
    } catch (Exception e) {
      LOGGER.error("zstd compare excetion", e);
    }
    return null;
  }

  public static String compressString(String s) {
    if (Strings.isEmpty(s)) {
      return null;
    }
    try {
      byte[] compressByte = compress(s.getBytes());
      if (compressByte == null) {
        return null;
      }
      return Base64.getEncoder().encodeToString(compressByte);
    } catch (Exception e) {
      LOGGER.error("zstd compressString excetion", e);
    }
    return null;
  }

  public static byte[] uncompress(byte[] bytes) {
    if (bytes == null) {
      return new byte[0];
    }
    try {
      try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
        try (InputStream inputStream = new ZstdInputStream(byteArrayInputStream)) {
          ByteArrayOutputStream out = new ByteArrayOutputStream(ONE_K_BUFFER_SIZE);
          byte[] buffer = new byte[ONE_K_BUFFER_SIZE];
          int n;
          while ((n = inputStream.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
          }
          return out.toByteArray();
        }
      }
    } catch (Exception e) {
      LOGGER.error("zstd uncompress excetion", e);
    }
    return null;
  }

  public static String uncompressString(String value) {
    if (Strings.isEmpty(value)) {
      return null;
    }

    try {
      byte[] uncompress = uncompress(Base64.getDecoder().decode(value));
      if (uncompress == null) {
        return null;
      }
      return new String(uncompress);
    } catch (Exception e) {
      LOGGER.error("zstd uncompressString excetion", e);
    }
    return null;
  }
}
