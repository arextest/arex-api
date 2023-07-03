package com.arextest.web.model.mapper;

import com.arextest.web.model.dao.mongodb.ApplicationCollection;
import com.arextest.web.model.dto.ApplicationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApplicationMapper extends BaseMapper {
    ApplicationMapper INSTANCE = Mappers.getMapper(ApplicationMapper.class);

    @Mappings({
            @Mapping(target = "dataChangeCreateTime", expression = "java("
                    + "dto.getDataChangeCreateTime() == null"
                    + "?System.currentTimeMillis()"
                    + ":dto.getDataChangeCreateTime())"
            ),
            @Mapping(target = "dataChangeUpdateTime", expression = "java("
                    + "dto.getDataChangeUpdateTime() == null"
                    + "?System.currentTimeMillis()"
                    + ":dto.getDataChangeUpdateTime())"
            ),

            @Mapping(target = "contract", qualifiedByName = "compressMsg")

    })
    ApplicationCollection daoFromDto(ApplicationDto dto);

    @Mappings({
            @Mapping(target = "contract", qualifiedByName = "decompressMsg")
    })
    ApplicationDto dtoFromDao(ApplicationCollection dao);
}
