//package com.arextest.web.model.contract.contracts.config.record;
//
//
//import com.arextest.web.model.contract.contracts.config.AbstractConfiguration;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.util.Map;
//import java.util.Set;
//
///**
// * @author jmo
// * @since 2021/12/21
// * Indicate to RecordServiceConfig datatable
// */
//@Setter
//@Getter
//public class ServiceCollectConfiguration extends AbstractConfiguration {
//    private String appId;
//    /**
//     * The sample rate means for in 100 seconds should be occurred the number of records.
//     * example:
//     * if the value is 50,would be recorded 50 times in 100 seconds.
//     */
//    private int sampleRate;
//    /**
//     * Bit flag composed of bits that indicate which day of the week are enabled to recording.
//     * Day of the week that enabled to recording indicates which bit is 1
//     * MONDAY -> SUNDAY : the position of 0 -> the position of 6
//     */
//    private int allowDayOfWeeks;
//    /**
//     * the switch that controls the time class mock
//     * if timeMock is true, means agent will mock the classes of time such as java.time.Instant(now)
//     */
//    private boolean timeMock;
//    /**
//     * HH:mm  example: 00:01
//     */
//    private String allowTimeOfDayFrom;
//    /**
//     * HH:mm example: 23:59
//     */
//    private String allowTimeOfDayTo;
//
//    private Set<String> excludeServiceOperationSet;
//
//    /**
//     * Maximum number of recording machines for each group
//     */
//    private Integer recordMachineCountLimit;
//
//    /**
//     * Extended content for user-defined
//     */
//    private Map<String, String> extendField;
//}
