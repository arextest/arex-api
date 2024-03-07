package com.arextest.web.model.contract.contracts.config;

import com.arextest.config.model.dto.system.SystemConfiguration;
import lombok.Data;

/**
 * @author wildeslam.
 * @create 2023/9/26 13:50
 */
@Data
public class SaveSystemConfigRequestType {

  private SystemConfiguration systemConfig;
}
