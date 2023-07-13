package com.arextest.web.model.contract.contracts.config.instance;

import com.arextest.web.model.contract.contracts.common.enums.StatusType;
import com.arextest.web.model.contract.contracts.config.record.DynamicClassConfiguration;
import com.arextest.web.model.contract.contracts.config.record.ServiceCollectConfiguration;
import lombok.Data;

import java.util.List;

/**
 * @author b_yu
 * @since 2023/6/14
 */
@Data
public class AgentRemoteConfigurationResponse {
    private ServiceCollectConfiguration serviceCollectConfiguration;
    private List<DynamicClassConfiguration> dynamicClassConfigurationList;

    /**
     * Bit flag composed of bits that record/replay are enabled.
     * see {@link  StatusType }
     */
    private Integer status;
}