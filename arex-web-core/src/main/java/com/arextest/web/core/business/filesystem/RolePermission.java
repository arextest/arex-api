package com.arextest.web.core.business.filesystem;

import com.arextest.common.utils.DefaultJWTService;
import com.arextest.web.core.repository.UserWorkspaceRepository;
import com.arextest.web.model.dto.filesystem.UserWorkspaceDto;
import com.arextest.web.model.enums.RoleType;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class RolePermission {

  public static final Integer EDIT_WORKSPACE = RoleType.ADMIN;
  public static final Integer INVITE_TO_WORKSPACE = RoleType.ADMIN;
  public static final Integer VIEW_WORKSPACE = RoleType.VIEWER;
  public static final Integer EDIT_ITEM = RoleType.EDITOR;
  public static final Integer EDIT_ENVIRONMENT = RoleType.EDITOR;
  public static final Integer EDIT_LABEL = RoleType.EDITOR;

  @Resource
  private UserWorkspaceRepository userWorkspaceRepository;

  @Resource
  private DefaultJWTService defaultJWTService;

  private boolean checkPermission(Integer action, String userName, String workspaceId) {
    UserWorkspaceDto userWorkspaceDto = userWorkspaceRepository.queryUserWorkspace(userName,
        workspaceId);
    if (userWorkspaceDto == null) {
      return false;
    }
    if (userWorkspaceDto.getRole() <= action) {
      return true;
    }
    return false;
  }

  public boolean checkPermissionByToken(Integer action, String token, String workspaceId) {
    String userName = defaultJWTService.getUserName(token);
    if (StringUtils.isEmpty(userName)) {
      return false;
    }
    return checkPermission(action, userName, workspaceId);
  }
}
