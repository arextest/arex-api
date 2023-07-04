package com.arextest.accurate.controller;

import com.arextest.accurate.lib.DynamicTracing;
import com.arextest.accurate.lib.JavaProject;
import com.arextest.accurate.model.GitBasicRequest;
import com.arextest.accurate.model.TracingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.util.List;

@Slf4j
@Controller
@Scope("prototype")
@RequestMapping("/api/trace/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DynamicTraceController {

    @PostMapping(value = "/static", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public TracingResponse diffsBySpringAnnotation(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("replay"))
            return TracingResponse.exceptionResponse("git repository is empty or beginDate is empty. ");

        JavaProject project = new JavaProject();
        return project.DoDynamicTrace(request);
    }

    @PostMapping(value = "/dynamic", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public TracingResponse StackTracing(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("replay"))
            return TracingResponse.exceptionResponse("git repository is empty or beginDate is empty. ");

        try (Jedis jedis = new Jedis("10.5.153.1", 6379)) {
            String oneKey = "TRACE1:AREX-172-20-0-4-70338765707135";
            List<String> list = jedis.lrange(oneKey, 0, -1);
            DynamicTracing dynamicTracing = new DynamicTracing();
            String result = dynamicTracing.StackTracesToCallGraph(list, "com.arextest.");
            TracingResponse response = new TracingResponse();
            response.setData(result);
            return response;
        } catch (Exception err) {
            return TracingResponse.exceptionResponse(err.getMessage());
        }
    }

}
