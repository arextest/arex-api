package com.arextest.web.model.contract.contracts.common;

import lombok.Data;

import java.util.Date;

/**
 * @author wildeslam.
 * @create 2024/2/20 20:11
 */
@Data
public class DesensitizationJar {
    private String jarUrl;
    private String remark;
    private Date uploadDate;
}
