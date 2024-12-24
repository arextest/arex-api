package com.arextest.web.core.business;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.arextest.web.core.repository.UserRepository;
import com.arextest.web.model.dto.UserDto;

import lombok.RequiredArgsConstructor;

/**
 * @author: QizhengMo
 * @date: 2024/11/11 20:26
 * To record the login activities of users, such as login, token refresh
 */
@Service
@RequiredArgsConstructor
public class LoginActivityService {
  private final UserRepository userRepository;

  public void onEvent(String username, UserDto.ActivityType activityType) {
    UserDto.Activity activity = new UserDto.Activity();
    activity.setDate(new Date());
    activity.setType(activityType);
    userRepository.pushUserActivity(username, activity);
  }
}
