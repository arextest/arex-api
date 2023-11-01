package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.login.ModifyUserFavoriteAppRequestType;
import com.arextest.web.model.contract.contracts.login.QueryUserFavoriteAppResponseType;
import com.arextest.web.model.contract.contracts.login.UpdateUserProfileRequestType;
import com.arextest.web.model.contract.contracts.login.UserProfileResponseType;
import com.arextest.web.model.dao.mongodb.UserCollection;
import com.arextest.web.model.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  UserDto dtoFromDao(UserCollection dao);

  UserProfileResponseType contractFromDto(UserDto dto);

  UserDto dtoFromContract(UpdateUserProfileRequestType request);

  QueryUserFavoriteAppResponseType queryUserFavoriteAppFromDto(UserDto dto);

  UserDto dtoFromModifyUserFavoriteApp(ModifyUserFavoriteAppRequestType request);
}
