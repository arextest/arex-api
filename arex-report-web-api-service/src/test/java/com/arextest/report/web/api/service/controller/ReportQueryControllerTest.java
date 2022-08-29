// package com.arextest.report.web.api.service.controller;
//
// import com.arextest.common.model.response.Response;
// import com.arextest.report.model.api.contracts.PushCompareResultsRequestType;
// import com.arextest.report.model.api.contracts.QueryCategoryStatisticRequestType;
// import com.arextest.report.model.api.contracts.QueryPlanItemStatisticsRequestType;
// import com.arextest.report.model.api.contracts.QueryPlanStatisticsRequestType;
// import com.arextest.report.model.api.contracts.ReportInitialRequestType;
// import com.arextest.report.model.api.contracts.common.CompareResult;
// import com.arextest.report.model.api.contracts.common.LogEntity;
// import com.arextest.report.model.api.contracts.common.NodeEntity;
// import com.arextest.report.model.api.contracts.common.UnmatchedPairEntity;
// import com.arextest.report.model.api.contracts.common.UnmatchedType;
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.junit4.SpringRunner;
//
// import javax.annotation.Resource;
// import java.util.ArrayList;
// import java.util.List;
//
// import static org.junit.Assert.assertNotNull;
// import static org.junit.Assert.assertTrue;
//
//
// @SpringBootTest
// @RunWith(SpringRunner.class)
// public class ReportQueryControllerTest {
//     @Resource
//     private ReportQueryController controller;
//
//     @Test
//     public void queryPlanStatistics() {
//         QueryPlanStatisticsRequestType request = new QueryPlanStatisticsRequestType();
//         request.setPageIndex(0);
//         request.setPageSize(4);
//         request.setNeedTotal(true);
//
//         Response response = controller.queryPlanStatistics(request);
//         assertTrue(response != null && response.getResponseStatusType().getResponseCode() == 0);
//
//         request.setAppId("100031334");
//         response = controller.queryPlanStatistics(request);
//         assertTrue(response != null && response.getResponseStatusType().getResponseCode() == 0);
//
//         request.setAppId("100031334");
//         response = controller.queryPlanStatistics(request);
//         assertTrue(response != null && response.getResponseStatusType().getResponseCode() == 0);
//
//         request.setPlanId(1L);
//         response = controller.queryPlanStatistics(request);
//         assertTrue(response != null && response.getResponseStatusType().getResponseCode() == 0);
//
//         request.setAppId("100031334");
//         request.setPlanId(2L);
//         response = controller.queryPlanStatistics(request);
//         assertTrue(response != null && response.getResponseStatusType().getResponseCode() == 0);
//
//         request.setAppId("100031334");
//         request.setPlanId(3L);
//         response = controller.queryPlanStatistics(request);
//         assertTrue(response != null && response.getResponseStatusType().getResponseCode() == 0);
//     }
//
//     @Test
//     public void queryPlanItemStatistics() {
//         QueryPlanItemStatisticsRequestType request = new QueryPlanItemStatisticsRequestType();
//         request.setPlanId(2L);
//         Response response = controller.queryPlanItemStatistics(request);
//         assertTrue(response != null && response.getResponseStatusType().getResponseCode() == 0);
//     }
//
//     @Test
//     public void queryResponseTypeStatistic() {
//         QueryCategoryStatisticRequestType request = new QueryCategoryStatisticRequestType();
//         request.setPlanItemId(0L);
//         Response response = controller.queryResponseTypeStatistic(request);
//         assertTrue(response != null && response.getResponseStatusType().getResponseCode() == 0);
//     }
//
//     @Test
//     public void testGetPushCompareResultsRequestType() throws JsonProcessingException {
//         List<CompareResult> results = new ArrayList<>();
//         CompareResult result = new CompareResult();
//         result.setDiffResultCode(1);
//         result.setCategoryName("SOA");
//         result.setBaseMsg("encoded base message");
//         result.setTestMsg("encoded compared message");
//         result.setPlanId(1L);
//         result.setPlanItemId(1L);
//         result.setRecordId("recordId");
//         result.setReplayId("replayId");
//         result.setOperationId(1L);
//         result.setOperationName("operationName");
//         result.setServiceName("serviceName");
//
//         List<LogEntity> logs = new ArrayList<>();
//         LogEntity log = new LogEntity();
//         UnmatchedPairEntity unmatched = new UnmatchedPairEntity();
//         unmatched.setUnmatchedType(UnmatchedType.UNMATCHED);
//
//         List<NodeEntity> nodes = new ArrayList<>();
//         NodeEntity node = new NodeEntity();
//         node.setNodeName("nodeName");
//         node.setIndex(0);
//         nodes.add(node);
//         unmatched.setLeftUnmatchedPath(nodes);
//         unmatched.setRightUnmatchedPath(nodes);
//         log.setPathPair(unmatched);
//         logs.add(log);
//         result.setLogs(logs);
//
//         results.add(result);
//         PushCompareResultsRequestType request = new PushCompareResultsRequestType();
//         request.setResults(results);
//
//
//         ObjectMapper mapper = new ObjectMapper();
//         String requestStr = mapper.writeValueAsString(request);
//         assertNotNull(requestStr);
//     }
//
//     @Test
//     public void reportInitial() throws JsonProcessingException {
//         ReportInitialRequestType request = new ReportInitialRequestType();
//         request.setCreator("b_yu");
//         request.setPlanName("planName");
//         request.setPlanId(111111L);
//         ReportInitialRequestType.Application application = new ReportInitialRequestType.Application();
//         application.setAppId("appId");
//         application.setAppName("appName");
//         request.setApplication(application);
//
//         ReportInitialRequestType.CaseSourceEnvironment caseSourceEnv =
//                 new ReportInitialRequestType.CaseSourceEnvironment();
//         caseSourceEnv.setCaseSourceType(0);
//         caseSourceEnv.setCaseStartTime(System.currentTimeMillis());
//         caseSourceEnv.setCaseEndTime(System.currentTimeMillis());
//         request.setCaseSourceEnv(caseSourceEnv);
//
//         ReportInitialRequestType.HostEnvironment hostEnv = new ReportInitialRequestType.HostEnvironment();
//         hostEnv.setSourceEnv("sourceEnv");
//         hostEnv.setSourceHost("sourceHost");
//         hostEnv.setTargetEnv("targetEnv");
//         hostEnv.setTargetHost("targetHost");
//         request.setHostEnv(hostEnv);
//
//         ReportInitialRequestType.TargetImage targetImage = new ReportInitialRequestType.TargetImage();
//         targetImage.setTargetImageId("imageId");
//         targetImage.setTargetImageName("imageName");
//         request.setTargetImage(targetImage);
//
//         List<ReportInitialRequestType.ReportItem> reportItems = new ArrayList<>();
//         ReportInitialRequestType.ReportItem reportItem = new ReportInitialRequestType.ReportItem();
//         reportItem.setOperationName("operationName");
//         reportItem.setPlanItemId(1L);
//         reportItem.setOperationId(2L);
//         reportItem.setTotalCaseCount(100);
//         reportItem.setServiceName("serviceName");
//         reportItems.add(reportItem);
//
//         reportItem = new ReportInitialRequestType.ReportItem();
//         reportItem.setOperationName("operationName2");
//         reportItem.setPlanItemId(2L);
//         reportItem.setOperationId(4L);
//         reportItem.setTotalCaseCount(200);
//         reportItem.setServiceName("serviceName2");
//         reportItems.add(reportItem);
//
//         request.setReportItemList(reportItems);
//
//         Response response = controller.reportInitial(request);
//         assertNotNull(response);
//     }
// }
