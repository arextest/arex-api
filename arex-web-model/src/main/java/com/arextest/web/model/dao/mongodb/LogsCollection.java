package com.arextest.web.model.dao.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author b_yu
 * @since 2023/2/9
 */
@Data
@Document(collection = "logs")
public class LogsCollection {
}
