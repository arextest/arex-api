package com.arextest.web.core.repository;


import com.arextest.web.model.dto.UserDto;

import java.util.List;

public interface UserRepository extends RepositoryProvider {
    Boolean saveUser(UserDto user);

    Boolean verify(String userName, String verificationCode);

    UserDto queryUserProfile(String userName);

    Boolean updateUserProfile(UserDto user);

    Boolean existUserName(String userName);

    Boolean insertUserFavoriteApp(String userName, String favoriteApp);

    Boolean removeUserFavoriteApp(String userName, String favoriteApp);

    List<UserDto> listUsers();
}
