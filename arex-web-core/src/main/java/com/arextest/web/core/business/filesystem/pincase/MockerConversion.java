package com.arextest.web.core.business.filesystem.pincase;

import com.arextest.model.mock.AREXMocker;
import com.arextest.web.model.dto.filesystem.FSCaseDto;

/**
 * @author b_yu
 * @since 2022/12/12
 */
public interface MockerConversion {
    String getCategoryName();

    FSCaseDto mockerConvertToFsCase(AREXMocker mocker);
}
