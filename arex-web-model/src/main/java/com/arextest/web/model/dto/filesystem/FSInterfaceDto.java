package com.arextest.web.model.dto.filesystem;

import com.arextest.web.model.dto.KeyValuePairDto;
import lombok.Data;

import java.util.List;

@Data
public class FSInterfaceDto extends FSItemDto {
    private AddressDto address;
    private String preRequestScript;
    private String testScript;
    private BodyDto body;
    private List<KeyValuePairDto> headers;
    private List<KeyValuePairDto> params;
    private AuthDto auth;
    private AddressDto testAddress;
}
