package com.arextest.web.model.dto.filesystem;

import com.arextest.web.model.dto.KeyValuePairDto;
import lombok.Data;

import java.util.List;

@Data
public class FSCaseDto extends FSInterfaceDto {
    private ComparisonMsgDto comparisonMsg;
    private String description;
}
