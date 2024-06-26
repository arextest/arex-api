package com.arextest.web.core.business.filesystem.pincase;

import com.arextest.model.mock.AREXMocker;
import com.arextest.model.mock.Mocker;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.constants.NetworkConstants;
import com.arextest.web.model.dto.KeyValuePairDto;
import com.arextest.web.model.dto.filesystem.AddressDto;
import com.arextest.web.model.dto.filesystem.BodyDto;
import com.arextest.web.model.dto.filesystem.FSCaseDto;
import com.arextest.web.model.dto.filesystem.FormDataDto;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

/**
 * @author b_yu
 * @since 2022/12/12
 */
@Slf4j
public class ServletMockerConversionImpl implements MockerConversion {

  private static final String AND = "&";
  private static final String EQUAL = "=";
  private static final String HTTP_METHOD = "HttpMethod";
  private static final String REQUEST_PATH = "RequestPath";
  private static final String SERVLET = "Servlet";
  private static final String FORM_DATA_TYPE_TEXT = "text";

  @Override
  public String getCategoryName() {
    return SERVLET;
  }

  @Override
  public FSCaseDto mockerConvertToFsCase(AREXMocker mocker) {
    if (mocker == null) {
      return null;
    }
    FSCaseDto caseDto = new FSCaseDto();

    Mocker.Target targetRequest = mocker.getTargetRequest();
    if (targetRequest == null) {
      return null;
    }

    try {
      AddressDto addressDto = new AddressDto();
      String method = targetRequest.getAttribute(HTTP_METHOD).toString();
      addressDto.setMethod(method);
      addressDto.setEndpoint(targetRequest.getAttribute(REQUEST_PATH).toString());
      caseDto.setAddress(addressDto);

      String contentType = StringUtils.EMPTY;

      Map<String, String> headers = (Map<String, String>) targetRequest.getAttribute("Headers");
      if (headers != null) {
        List<KeyValuePairDto> kvPair = new ArrayList<>(headers.size());
        for (Map.Entry<String, String> header : headers.entrySet()) {
          if (StringUtils.equalsIgnoreCase(header.getKey(), NetworkConstants.CONTENT_TYPE)) {
            contentType = header.getValue();
          }
          KeyValuePairDto kv = new KeyValuePairDto();
          kv.setKey(header.getKey());
          kv.setValue(header.getValue());
          kv.setActive(true);
          kvPair.add(kv);
        }
        caseDto.setHeaders(kvPair);
      }

      if (StringUtils.isNotBlank(targetRequest.getBody())) {
        if (Objects.equals(method, HttpMethod.GET.name())) {
          String[] params = targetRequest.getBody().split(AND);
          List<KeyValuePairDto> paramKvPair = new ArrayList<>();
          for (String param : params) {
            String[] kv = param.split(EQUAL);
            if (kv.length != 2) {
              continue;
            }
            KeyValuePairDto paramKv = new KeyValuePairDto();
            paramKv.setKey(kv[0]);
            paramKv.setValue(kv[1]);
            paramKv.setActive(true);
            paramKvPair.add(paramKv);
          }
          caseDto.setParams(paramKvPair);
        } else {
          BodyDto bodyDto = new BodyDto();
          bodyDto.setContentType(contentType);
          if (contentType.toLowerCase().contains(NetworkConstants.APPLICATION_JSON)) {
            try {
              bodyDto.setBody(new String(Base64.getDecoder().decode(targetRequest.getBody())));
            } catch (Exception e) {
              bodyDto.setBody(targetRequest.getBody());
            }
          } else {
            bodyDto.setBody(targetRequest.getBody());
          }

          // set formData
          if (contentType.toLowerCase().contains(NetworkConstants.FORM_DATA)) {
            String formData = new String(Base64.getDecoder().decode(targetRequest.getBody()));
            bodyDto.setFormData(formatFormData(formData));
          }

          caseDto.setBody(bodyDto);
        }
      }

      return caseDto;
    } catch (Exception e) {
      LogUtils.error(LOGGER, "Failed to convert AREXMocker to FSCaseDto", e);
      return null;
    }
  }

  private List<FormDataDto> formatFormData(String formData) {
    List<FormDataDto> formDataDtos = new ArrayList<>();
    String[] formDatas = formData.split(AND);
    for (String data : formDatas) {
      String[] kv = data.split(EQUAL);
      if (kv.length != 2) {
        continue;
      }
      FormDataDto formDataDto = new FormDataDto();
      formDataDto.setKey(kv[0]);
      formDataDto.setValue(kv[1]);
      formDataDto.setType(FORM_DATA_TYPE_TEXT);
      formDataDtos.add(formDataDto);
    }
    return formDataDtos;
  }
}
