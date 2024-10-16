//package com.arextest.web.api.service.beans;
//
//import com.arextest.web.api.service.WebSpringBootServletInitializer;
//import com.arextest.web.core.repository.SceneInfoRepository;
//import com.arextest.web.model.dao.mongodb.iosummary.SceneInfoCollection;
//import com.arextest.web.model.dto.iosummary.DiffDetail;
//import com.arextest.web.model.dto.iosummary.SceneInfo;
//import com.arextest.web.model.dto.iosummary.SubSceneInfo;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import javax.annotation.Resource;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@SpringBootTest(classes = WebSpringBootServletInitializer.class)
//@RunWith(SpringRunner.class)
//public class SceneInfoTest {
//
//  @Resource
//  SceneInfoRepository sceneInfoRepository;
//
//  @Resource
//  MongoTemplate mongoTemplate;
//
//  @Test
//  public void test() throws InterruptedException {
//
////    ExecutorService executorService = Executors.newFixedThreadPool(4);
////    for (int i = 0; i < 1000; i++) {
////      executorService.submit(this::testFunc);
////    }
////
////    executorService.shutdown();
////    executorService.awaitTermination(1, TimeUnit.HOURS);
////    removeDetails();
//    testFunc();
//  }
//
//
//  private void removeDetails() {
//    Query query = new Query();
//    query.addCriteria(Criteria.where("id").is("670e61d487d688c9ee905b1d"));
//    SceneInfoCollection sceneInfo = mongoTemplate.findOne(query, SceneInfoCollection.class);
//    sceneInfo.getSubSceneInfoMap().forEach((k, v) -> {
//      v.setDetails(null);
//    });
//    SceneInfoCollection save = mongoTemplate.save(sceneInfo);
//    System.out.println();
//  }
//
//  private void testFunc() {
//    long start = System.currentTimeMillis();
//    SceneInfo sceneInfo = new SceneInfo();
//    sceneInfo.setCode(5);
//
////    sceneInfo.setCategoryKey(4002294653873329176L);
//
////    Random random = new Random();
////    if (random.nextDouble() < 0.3) {
////      sceneInfo.setCategoryKey(random.nextLong());
////    } else {
////      sceneInfo.setCategoryKey(4002294653873329176L);
////    }
//
//    sceneInfo.setCategoryKey(4002294653873329176L);
//
//    sceneInfo.setPlanId("66e52744b17a10171205456d");
//    sceneInfo.setPlanItemId("66e52744b17a101712054579");
//
//    Map<String, SubSceneInfo> sceneInfoMap = new HashMap<>();
//    SubSceneInfo subSceneInfo = new SubSceneInfo();
//    subSceneInfo.setCode(5);
//    subSceneInfo.setRecordId("AREX-10-40-137-158-501001646495472");
//    subSceneInfo.setReplayId("AREX-10-118-231-156-844553916818");
//
//    List<DiffDetail> diffDetails = new ArrayList<>();
//    diffDetails.add(new DiffDetail(1, "SOAProvider",
//        "hotel.booking.availservice.v1.hotelreservationavailservice.multiRoomAvail"));
//    diffDetails.add(new DiffDetail(4, "SOAConsumer",
//        "hotel.vendor.vendorbaseservice.v1.vendorbaseservice.checkIfInterfaceHotel"));
//    diffDetails.add(new DiffDetail(4, "SOAConsumer",
//        "hotel.queryws.productservice.v1.hotelproductservice.getHotelStaticInfo"));
//    diffDetails.add(new DiffDetail(4, "SOAConsumer",
//        "hotel.product.searchservice.v1.hotelproductsearchjavaservice.hotelRatePlan"));
//
//    diffDetails.add(
//        new DiffDetail(4, "SOAConsumer", "hotel.product.v1.htlproductservice.getRoomType"));
//    diffDetails.add(new DiffDetail(4, "SOAConsumer",
//        "hotel.product.hotelinfoservice.v1.hotelinfoservice.getHotelInfo"));
//    diffDetails.add(new DiffDetail(4, "SOAConsumer",
//        "platform.crm.myintegration.v1.integrationservice.getAvailablePoints"));
//    diffDetails.add(new DiffDetail(4, "SOAConsumer",
//        "platform.accountsecurityservice.integrationgrade.v1.integrationmembergradews.getCurrentLevel_test"));
//
//    subSceneInfo.setDetails(diffDetails);
//    sceneInfoMap.put("6710692139422438468", subSceneInfo);
//    sceneInfo.setSubSceneInfoMap(sceneInfoMap);
//    SceneInfo save = sceneInfoRepository.save(sceneInfo);
//    System.out.println(System.currentTimeMillis() - start);
//  }
//
//}
