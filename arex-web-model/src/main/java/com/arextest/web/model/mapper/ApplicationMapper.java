package com.arextest.web.model.mapper;

import com.arextest.web.model.dao.mongodb.AppContractCollection;
import com.arextest.web.model.dto.AppContractDto;
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
    AppContractCollection daoFromDto(AppContractDto dto);

    @Mappings({
            @Mapping(target = "contract", qualifiedByName = "decompressMsg")
    })
    AppContractDto dtoFromDao(AppContractCollection dao);
}
