package com.arextest.web.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wildeslam.
 * @create 2023/10/7 15:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseUserDto {
    private String id;
    private String userName;
}
