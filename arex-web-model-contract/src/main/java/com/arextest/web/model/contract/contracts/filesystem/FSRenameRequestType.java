package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class FSRenameRequestType {
    @NotBlank(message = "Id cannot be empty")
    private String id;
    @NotNull(message = "Node path cannot be empty")
    @Size(min = 1, message = "Path size must be greater than 0")
    private String[] path;
    @NotBlank(message = "New name cannot be empty")
    private String newName;
    private String userName;
}
