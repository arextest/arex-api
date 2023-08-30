package com.arextest.web.model.dto;

import lombok.Data;

import java.util.Date;


/**
 * @author qzmo
 * @since 2023/08/16
 */
@Data
public class DesensitizationJarDto extends BaseDto {
    private String jarUrl;
    private String remark;
    private Date uploadDate;
}
