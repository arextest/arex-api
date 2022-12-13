package com.arextest.web.core.business.filesystem.pincase;

import com.arextest.model.mock.AREXMocker;
import com.arextest.model.mock.Mocker;
import com.arextest.web.model.dto.KeyValuePairDto;
import com.arextest.web.model.dto.filesystem.AddressDto;
import com.arextest.web.model.dto.filesystem.BodyDto;
import com.arextest.web.model.dto.filesystem.FSCaseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.swing.text.html.parser.Entity;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author b_yu
 * @since 2022/12/12
 */
@Slf4j
public class ServletMockerConversionImpl implements MockerConversion {

    private static final String AND = "&";
    private static final String EQUAL = "=";
    private static final String HTTP_METHOD = "HttpMethod";
    private static final String SERVLET_PATH = "ServletPath";
    private static final String PREFIX_URL = "http://{{}}";
    private static final String SERVLET = "Servlet";

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
            addressDto.setEndpoint(PREFIX_URL + targetRequest.getAttribute(SERVLET_PATH).toString());
            caseDto.setAddress(addressDto);

            Map<String, String> headers = (Map<String, String>) targetRequest.getAttribute("Headers");
            if (headers != null) {
                List<KeyValuePairDto> kvPair = new ArrayList<>(headers.size());
                for (Map.Entry<String, String> header : headers.entrySet()) {
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
                    bodyDto.setBody(targetRequest.getBody());
                    caseDto.setBody(bodyDto);
                }
            }

            return caseDto;
        } catch (Exception e) {
            LOGGER.error("Failed to convert AREXMocker to FSCaseDto", e);
            return null;
        }
    }
}
