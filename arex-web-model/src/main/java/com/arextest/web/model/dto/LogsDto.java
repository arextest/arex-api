package com.arextest.web.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author b_yu
 * @since 2023/2/10
 */
@Data
public class LogsDto {
    private String id;
    private String level;
    private String loggerName;
    private String message;
    private long threadId;
    private String threadName;
    private int threadPriority;
    private long millis;
    private Map<String, String> contextMap;
    private UnitDto source;
    private ThrownDto thrown;


    @Data
    public static final class UnitDto {
        private String className;
        private String methodName;
        private String fileName;
        private int lineNumber;
    }


    @Data
    public static final class ThrownDto {
        private String type;
        private String message;
        private List<UnitDto> stackTrace;
    }
}
