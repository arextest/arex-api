package com.arextest.report.model.dao.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author b_yu
 * @since 2022/11/17
 */
@Data
@Document(collection = "Label")
public class LabelCollection extends ModelBase {
    private String labelName;
    private String color;
    private String workspaceId;
}
