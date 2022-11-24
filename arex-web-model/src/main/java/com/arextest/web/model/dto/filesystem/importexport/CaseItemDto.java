package com.arextest.web.model.dto.filesystem.importexport;

import com.arextest.web.model.dto.KeyValuePairDto;
import com.arextest.web.model.dto.filesystem.AddressDto;
import com.arextest.web.model.dto.filesystem.AuthDto;
import com.arextest.web.model.dto.filesystem.BodyDto;
import lombok.Data;

import java.util.List;

@Data
public class CaseItemDto implements Item {
    private String nodeName;
    private Integer nodeType;
    private AddressDto address;
    private String preRequestScript;
    private String testScript;
    private BodyDto body;
    private List<KeyValuePairDto> headers;
    private List<KeyValuePairDto> params;
    private AuthDto auth;
    private AddressDto testAddress;
    private List<Item> items;
}
