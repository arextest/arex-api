package com.arextest.web.model.dto;

import java.util.Date;

import lombok.Data;

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
