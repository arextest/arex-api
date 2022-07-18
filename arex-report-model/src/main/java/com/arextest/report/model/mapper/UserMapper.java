package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.login.UpdateUserProfileRequestType;
import com.arextest.report.model.api.contracts.login.UserProfileResponseType;
import com.arextest.report.model.dao.mongodb.UserCollection;
import com.arextest.report.model.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto dtoFromDao(UserCollection dao);

    UserProfileResponseType contractFromDto(UserDto dto);

    UserDto dtoFromContract(UpdateUserProfileRequestType request);
}
