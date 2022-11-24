package com.arextest.web.model.dto;

import lombok.Data;

/**
 * @author b_yu
 * @since 2022/11/17
 */
@Data
public class LabelDto extends BaseDto {
    private String labelName;
    private String color;
    private String workspaceId;
}
