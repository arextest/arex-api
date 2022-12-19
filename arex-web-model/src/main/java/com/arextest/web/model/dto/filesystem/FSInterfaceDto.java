package com.arextest.web.model.dto.filesystem;

import com.arextest.web.model.dto.KeyValuePairDto;
import lombok.Data;

import java.util.List;

@Data
public class FSInterfaceDto extends FSItemDto {
    private AddressDto address;
    private List<ScriptBlockDto> preRequestScripts;
    private List<ScriptBlockDto> testScripts;
    private BodyDto body;
    private List<KeyValuePairDto> headers;
    private List<KeyValuePairDto> params;
    private AuthDto auth;
    private AddressDto testAddress;
    private String recordId;
    private String operationId;
}
