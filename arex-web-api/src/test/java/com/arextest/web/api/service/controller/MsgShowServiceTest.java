package com.arextest.web.api.service.controller;

import com.arextest.web.api.service.WebSpringBootServletInitializer;
import com.arextest.web.core.business.MsgShowService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

/**
 * Created by rchen9 on 2023/3/8.
 */
@SpringBootTest(classes = WebSpringBootServletInitializer.class)
@RunWith(SpringRunner.class)
public class MsgShowServiceTest {

    @Autowired
    private MsgShowService msgShowService;

    @Test
    public void testProduceNewObjectFromOriginal() {
        String baseMsg = "{\"a\":\"b\"}";
        String testMsg = "aaaa";
        msgShowService.produceNewObjectFromOriginal(baseMsg, testMsg, new ArrayList<>());

    }
}
