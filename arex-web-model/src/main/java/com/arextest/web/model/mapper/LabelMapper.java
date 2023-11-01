package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.label.LabelType;
import com.arextest.web.model.contract.contracts.label.SaveLabelRequestType;
import com.arextest.web.model.dao.mongodb.LabelCollection;
import com.arextest.web.model.dto.LabelDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author b_yu
 * @since 2022/11/17
 */
@Mapper
public interface LabelMapper {

  LabelMapper INSTANCE = Mappers.getMapper(LabelMapper.class);

  LabelCollection daoFromDto(LabelDto dto);

  LabelDto dtoFromDao(LabelCollection dao);

  LabelDto dtoFromContract(SaveLabelRequestType request);

  LabelType contractFromDto(LabelDto dto);
}
