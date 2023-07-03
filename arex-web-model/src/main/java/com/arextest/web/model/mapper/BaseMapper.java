package com.arextest.web.model.mapper;

import com.arextest.web.common.ZstdUtils;
import org.mapstruct.Named;

public interface BaseMapper {
    @Named("compressMsg")
    default String compressMsg(String decompressString) {
        return ZstdUtils.compressString(decompressString);
    }

    @Named("decompressMsg")
    default String decompressMsg(String compressString) {
        return ZstdUtils.uncompressString(compressString);
    }
}
