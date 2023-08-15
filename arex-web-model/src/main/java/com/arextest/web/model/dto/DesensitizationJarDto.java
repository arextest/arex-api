package com.arextest.web.model.dto;

import lombok.Data;


/**
 * @author b_yu
 * @since 2022/11/17
 */
@Data
public class DesensitizationJarDto extends BaseDto {
    private String jarUrl;
    private String remark;
}
