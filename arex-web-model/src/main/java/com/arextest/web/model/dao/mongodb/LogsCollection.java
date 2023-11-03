package com.arextest.web.model.dao.mongodb;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author b_yu
 * @since 2023/2/9
 */
@Data
@Document(collection = "logs")
public class LogsCollection {

  @Id
  private String id;
  private String level;
  private String loggerName;
  private String message;
  private long threadId;
  private String threadName;
  private int threadPriority;
  private long millis;
  private Map<String, String> contextMap;
  private Unit source;
  private Thrown thrown;

  @Data
  public static final class Unit {

    private String className;
    private String methodName;
    private String fileName;
    private int lineNumber;
  }

  @Data
  public static final class Thrown {

    private String type;
    private String message;
    private List<Unit> stackTrace;
  }
}
