package com.arextest.report.model.dto.filesystem;

import com.arextest.report.model.dto.KeyValuePairDto;
import lombok.Data;

import java.util.List;

@Data
public class FSInterfaceDto {
    private String id;
    private AddressDto address;
    private String preRequestScript;
    private String testScript;
    private BodyDto body;
    private List<KeyValuePairDto> headers;
    private List<KeyValuePairDto> params;
    private AuthDto auth;
    private AddressDto baseAddress;
    private AddressDto testAddress;
}
