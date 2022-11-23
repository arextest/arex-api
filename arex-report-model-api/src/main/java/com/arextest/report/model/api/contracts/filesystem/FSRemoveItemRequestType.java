package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class FSRemoveItemRequestType {
    @NotBlank(message = "Id cannot be empty")
    private String id;
    @NotNull(message = "Remove node path cannot be empty")
    @Size(min = 1, message = "Remove path size must be greater than 0")
    private String[] removeNodePath;
    private String userName;
}
