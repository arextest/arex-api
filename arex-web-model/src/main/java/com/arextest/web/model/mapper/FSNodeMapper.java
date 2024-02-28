package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.filesystem.FSNodeType;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import com.arextest.web.model.dto.filesystem.FSItemDto;
import com.arextest.web.model.dto.filesystem.FSNodeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FSNodeMapper {

  FSNodeMapper INSTANCE = Mappers.getMapper(FSNodeMapper.class);

  FSNodeDto copy(FSNodeDto dto);

  FSNodeType contractFromDto(FSNodeDto dto);

  @Mapping(target = "infoId", source = "id")
  @Mapping(target = "nodeName", source = "name")
  FSNodeType contractFromFSItemDto(FSItemDto dto);


  @Mapping(target = "infoId", source = "id")
  @Mapping(target = "nodeName", source = "name")
  @Mapping(target = "method", source = "address.method")
  FSNodeType contractFromFSInterfaceDto(FSInterfaceDto dto);
}
