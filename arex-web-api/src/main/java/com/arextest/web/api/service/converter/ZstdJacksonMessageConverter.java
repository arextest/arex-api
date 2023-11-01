package com.arextest.web.api.service.converter;

import static org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter.DEFAULT_CHARSET;

import com.arextest.common.utils.SerializationUtils;
import com.arextest.web.model.contract.contracts.PushCompareResultsRequestType;
import java.io.IOException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

@Component
public final class ZstdJacksonMessageConverter extends AbstractHttpMessageConverter<Object> {

  public static final String ZSTD_JSON_MEDIA_TYPE = "application/zstd-json;charset=UTF-8";

  public ZstdJacksonMessageConverter() {
    super(DEFAULT_CHARSET, MediaType.parseMediaType(ZSTD_JSON_MEDIA_TYPE));
  }

  @Override
  protected boolean supports(Class<?> clazz) {
    if (PushCompareResultsRequestType.class == clazz) {
      return true;
    }
    return false;
    // return !(clazz == byte[].class || clazz.isPrimitive());
  }

  @Override
  protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
      throws IOException, HttpMessageNotReadableException {
    return SerializationUtils.useZstdDeserialize(inputMessage.getBody(), clazz);
  }

  @Override
  protected void writeInternal(Object o, HttpOutputMessage outputMessage)
      throws IOException, HttpMessageNotWritableException {
    SerializationUtils.useZstdSerializeTo(outputMessage.getBody(), o);
  }
}
