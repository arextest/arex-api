package com.arextest.web.core.business.filesystem.recovery;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

/**
 * @author b_yu
 * @since 2023/2/7
 */
@Component
public class RecoveryFactory {
    private static final String RECOVERY_DASH = "Recovery-";

    @Resource
    private Map<String, RecoveryService> recoveryMap;

    public RecoveryService getRecoveryService(Integer type) {
        String key = RECOVERY_DASH + type.toString();
        if (recoveryMap.containsKey(key)) {
            return recoveryMap.get(key);
        }
        return null;
    }
}
