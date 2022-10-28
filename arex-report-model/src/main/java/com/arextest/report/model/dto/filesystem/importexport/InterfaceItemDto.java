package com.arextest.report.model.dto.filesystem.importexport;

import com.arextest.report.model.dto.KeyValuePairDto;
import com.arextest.report.model.dto.filesystem.AddressDto;
import com.arextest.report.model.dto.filesystem.AuthDto;
import com.arextest.report.model.dto.filesystem.BodyDto;
import lombok.Data;

import java.util.List;

@Data
public class InterfaceItemDto implements Item {
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
