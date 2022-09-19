package com.arextest.report.core.business.filesystem;

import com.arextest.report.common.JwtUtil;
import com.arextest.report.core.repository.UserWorkspaceRepository;
import com.arextest.report.model.dto.filesystem.UserWorkspaceDto;
import com.arextest.report.model.enums.RoleType;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class RolePermission {
    public static final Integer EDIT_WORKSPACE = 1;
    public static final Integer INVITE_TO_WORKSPACE = 2;
    public static final Integer EDIT_ITEM = 10;
    public static final Integer EDIT_ENVIRONMENT = 20;

    private Map<Integer, Integer> actionRoleMap;

    @Resource
    private UserWorkspaceRepository userWorkspaceRepository;

    public RolePermission() {
        actionRoleMap = new HashMap<>();
        actionRoleMap.put(EDIT_WORKSPACE, RoleType.ADMIN);
        actionRoleMap.put(INVITE_TO_WORKSPACE, RoleType.ADMIN);
        actionRoleMap.put(EDIT_ITEM, RoleType.EDITOR);
        actionRoleMap.put(EDIT_ENVIRONMENT, RoleType.EDITOR);
    }

    private boolean checkPermission(Integer action, String userName, String workspaceId) {
        UserWorkspaceDto userWorkspaceDto = userWorkspaceRepository.queryUserWorkspace(userName, workspaceId);
        if (userWorkspaceDto == null) {
            return false;
        }
        if (!actionRoleMap.containsKey(action)) {
            return false;
        }
        if (userWorkspaceDto.getRole() <= actionRoleMap.get(action)) {
            return true;
        }
        return false;
    }

    public boolean checkPermissionByToken(Integer action, String token, String workspaceId) {
        String userName = JwtUtil.getUserName(token);
        if (StringUtils.isEmpty(userName)) {
            return false;
        }
        return checkPermission(action, userName, workspaceId);
    }
}
