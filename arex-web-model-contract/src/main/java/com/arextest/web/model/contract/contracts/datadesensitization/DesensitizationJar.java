package com.arextest.web.model.contract.contracts.datadesensitization;

import lombok.Data;

import java.util.Date;


/**
 * @author qzmo
 * @since 2023/08/16
 */
@Data
public class DesensitizationJar {
    private String id;
    private String jarUrl;
    private String remark;
    private Date uploadDate;
}
