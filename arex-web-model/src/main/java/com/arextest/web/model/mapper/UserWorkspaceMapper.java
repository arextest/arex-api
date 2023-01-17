package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.filesystem.ChangeRoleRequestType;
import com.arextest.web.model.contract.contracts.filesystem.InviteToWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.UserType;
import com.arextest.web.model.contract.contracts.filesystem.ValidInvitationRequestType;
import com.arextest.web.model.dao.mongodb.UserWorkspaceCollection;
import com.arextest.web.model.dto.filesystem.UserWorkspaceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserWorkspaceMapper {
    UserWorkspaceMapper INSTANCE = Mappers.getMapper(UserWorkspaceMapper.class);

    UserWorkspaceDto dtoFromDao(UserWorkspaceCollection dao);

    UserWorkspaceDto dtoFromContract(InviteToWorkspaceRequestType request);

    UserWorkspaceDto dtoFromContract(ValidInvitationRequestType request);

    UserWorkspaceDto dtoFromContract(ChangeRoleRequestType request);

    UserType userTypeFromDto(UserWorkspaceDto dto);
}
