package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.filesystem.FSAddItemsByAppAndInterfaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSPinMockRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSSaveCaseRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSSaveInterfaceRequestType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author wildeslam.
 * @create 2024/6/28 11:36
 */
@Mapper
public interface FSRequestMapper {

  FSRequestMapper INSTANCE = Mappers.getMapper(FSRequestMapper.class);

  FSSaveInterfaceRequestType buildSaveInterfaceRequest(
      FSAddItemsByAppAndInterfaceRequestType request);

  FSSaveCaseRequestType buildSaveCaseRequest(FSAddItemsByAppAndInterfaceRequestType request);

  @Mapping(target = "recordId", source = "nodeName")
  FSPinMockRequestType buildPinMockRequest(FSAddItemsByAppAndInterfaceRequestType request);

}
