package com.arextest.web.core.business.filesystem.pincase;

import com.arextest.model.mock.AREXMocker;
import com.arextest.model.mock.Mocker;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.constants.NetworkConstants;
import com.arextest.web.model.dto.KeyValuePairDto;
import com.arextest.web.model.dto.filesystem.AddressDto;
import com.arextest.web.model.dto.filesystem.BodyDto;
import com.arextest.web.model.dto.filesystem.FSCaseDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * @author wildeslam.
 * @create 2023/12/6 16:46
 */
@Slf4j
public class DubboProviderMockerConversionImpl implements MockerConversion {
    private static final String DUBBO = "Dubbo";

    @Override
    public String getCategoryName() {
        return "DubboProvider";
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
            addressDto.setMethod(DUBBO);
            addressDto.setEndpoint(mocker.getOperationName());
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
                caseDto.setBody(bodyDto);

            }

            return caseDto;
        } catch (Exception e) {
            LogUtils.error(LOGGER, "Failed to convert AREXMocker to FSCaseDto", e);
            return null;
        }
    }
}
