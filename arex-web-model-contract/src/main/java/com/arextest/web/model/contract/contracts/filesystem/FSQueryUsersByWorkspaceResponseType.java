package com.arextest.web.model.contract.contracts.filesystem;

import java.util.List;

import lombok.Data;

@Data
public class FSQueryUsersByWorkspaceResponseType {
    private List<UserType> users;
}
