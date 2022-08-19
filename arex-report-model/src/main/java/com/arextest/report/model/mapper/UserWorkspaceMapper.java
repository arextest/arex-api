package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.filesystem.InviteToWorkspaceRequestType;
import com.arextest.report.model.api.contracts.filesystem.UserType;
import com.arextest.report.model.api.contracts.filesystem.ValidInvitationRequestType;
import com.arextest.report.model.dao.mongodb.UserWorkspaceCollection;
import com.arextest.report.model.dto.filesystem.UserWorkspaceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserWorkspaceMapper {
    UserWorkspaceMapper INSTANCE = Mappers.getMapper(UserWorkspaceMapper.class);

    UserWorkspaceDto dtoFromDao(UserWorkspaceCollection dao);

    UserWorkspaceDto dtoFromContract(InviteToWorkspaceRequestType request);

    UserWorkspaceDto dtoFromContract(ValidInvitationRequestType request);

    UserType userTypeFromDto(UserWorkspaceDto dto);
}
