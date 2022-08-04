package com.arextest.report.core.business.filesystem;

import com.arextest.report.core.repository.UserWorkspaceRepository;
import com.arextest.report.model.dto.filesystem.UserWorkspaceDto;
import com.arextest.report.model.enums.RoleType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class RolePermission {
    public static final Integer DELETE_WORKSPACE_ACTION = 1;
    public static final Integer REMOVE_ITEM = 2;
    public static final Integer ADD_ITEM = 3;
    public static final Integer RENAME_ITEM = 4;
    public static final Integer DUPLICATE_ITEM = 5;
    public static final Integer RENAME_WORKSPACE = 6;
    public static final Integer INVITE_TO_WORKSPACE = 7;

    private Map<Integer, Integer> actionRoleMap;

    @Resource
    private UserWorkspaceRepository userWorkspaceRepository;

    public RolePermission() {
        actionRoleMap = new HashMap<>();
        actionRoleMap.put(DELETE_WORKSPACE_ACTION, RoleType.ADMIN);
        actionRoleMap.put(REMOVE_ITEM, RoleType.EDITOR);
        actionRoleMap.put(ADD_ITEM, RoleType.EDITOR);
        actionRoleMap.put(RENAME_ITEM, RoleType.EDITOR);
        actionRoleMap.put(DUPLICATE_ITEM, RoleType.EDITOR);
        actionRoleMap.put(RENAME_WORKSPACE, RoleType.ADMIN);
        actionRoleMap.put(INVITE_TO_WORKSPACE, RoleType.ADMIN);
    }

    public boolean checkPermission(Integer action, String userName, String workspaceId) {
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
}
