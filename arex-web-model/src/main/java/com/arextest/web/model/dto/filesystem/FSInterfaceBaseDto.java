package com.arextest.web.model.dto.filesystem;

import com.arextest.web.model.dto.KeyValuePairDto;
import lombok.Data;

import java.util.List;

/**
 * Created by rchen9 on 2023/1/9.
 */
@Data
public class FSInterfaceBaseDto extends FSItemDto {
    private AddressDto address;
    private List<ScriptBlockDto> preRequestScripts;
    private List<ScriptBlockDto> testScripts;
    private BodyDto body;
    private List<KeyValuePairDto> headers;
    private List<KeyValuePairDto> params;
    private AuthDto auth;
    private AddressDto testAddress;
}
