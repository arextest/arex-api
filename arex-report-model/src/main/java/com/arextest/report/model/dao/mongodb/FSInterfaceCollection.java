package com.arextest.report.model.dao.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "FSInterfaceCollection")
public class FSInterfaceCollection extends ModelBase {
}
