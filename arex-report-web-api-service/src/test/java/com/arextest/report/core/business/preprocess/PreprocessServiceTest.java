// package com.arextest.report.core.business.preprocess;
//
// import com.arextest.report.web.api.service.WebSpringBootServletInitializer;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.junit4.SpringRunner;
//
// import static org.junit.Assert.assertTrue;
//
// @RunWith(SpringRunner.class)
// @SpringBootTest(classes = WebSpringBootServletInitializer.class)
// public class PreprocessServiceTest {
//
//     @Autowired
//     PreprocessService service;
//
//     @Test
//     public void updateSchema() throws Exception {
//         boolean result = service.updateSchema("abc",
//                 "{\"baseValue\":{},\"testValue\":null,\"logInfo\":\"There is more node on the left : [parameter]\",\"pathPair\":{\"unmatchedType\":2,\"leftUnmatchedPath\":[{\"nodeName\":\"parameter\",\"index\":0}],\"rightUnmatchedPath\":[],\"listKeys\":[],\"listKeyPath\":[],\"trace\":{\"currentTraceLeft\":null,\"currentTraceRight\":null}},\"addRefPkNodePathLeft\":null,\"addRefPkNodePathRight\":null,\"warn\":0,\"path\":\"parameter\",\"logTag\":{\"lv\":1,\"ig\":false}}");
//         Thread.sleep(5000);
//         assertTrue(result);
//     }
// }