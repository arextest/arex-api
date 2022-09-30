package com.arextest.report.model.mapper;

import com.arextest.report.model.dto.filesystem.FSItemDto;
import com.arextest.report.model.dto.filesystem.importexport.Item;
import org.mapstruct.Mapper;

/**
 * @author b_yu
 * @since 2022/9/30
 */
public interface FSItemMapper {
    Item convertFromFsItemDto(FSItemDto fsItemDto);
}
