package com.arextest.accurate.controller;

import com.arextest.accurate.biz.CallTrace;
import com.arextest.accurate.biz.CodeAnalysis;
import com.arextest.accurate.lib.JCodeMethod;
import com.arextest.accurate.lib.JavaProject;
import com.arextest.accurate.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@Scope("prototype")
@RequestMapping("/api/analysis/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AnalysisController {

    @PostMapping(value = "/scan", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Response scan(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("commit"))
            return Response.exceptionResponse("request is empty. ");

        JavaProject javaProject = CodeAnalysis.ScanJava(request.getRepositoryURL(), "");
        if (javaProject == null)
            return Response.exceptionResponse("init Project failed");

        return javaProject.DoCodeScan(request);
    }


    /**
     * 获取变更的函数清单
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/diffMethods", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public TracingResponse diffMethods(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("listCommit")) {
            return TracingResponse.exceptionResponse("git repository is empty or beginDate is empty. ");
        }

        List<JCodeMethod> methods = CodeAnalysis.ScanChangedMethods(request.getRepositoryURL(), request.getBranch(), request.getNewCommit(), request.getOldCommit());
        TracingResponse tracingResponse = new TracingResponse();
        tracingResponse.setData(methods);
        return tracingResponse;
    }

    @PostMapping(value = "/spring", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public TracingResponse traceSpring(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("listCommit")) {
            return TracingResponse.exceptionResponse("git repository is empty or beginDate is empty. ");
        }

        JavaProject jp = CodeAnalysis.ScanJava(request.getRepositoryURL(), request.getBranch());
        if (jp == null) {
            TracingResponse tracingResponse = new TracingResponse();
            tracingResponse.setStatusCode(10001);
            tracingResponse.setResult("clone git first");
            return tracingResponse;
        }
        List<MethodTracing> result = jp.traceCallGraphInSpring();
        TracingResponse tracingResponse = new TracingResponse();
        tracingResponse.setStatusCode(20000);
        tracingResponse.setData(result);
        return tracingResponse;
    }

    /**
     * 查询指定的函数其调用图 Call Graph
     *
     * @param callGraphRequest 请求参数
     * @return List<String>目前只是字符串List
     */
    @PostMapping("/staticTrace")
    @ResponseBody
    public CallGraphResponse queryCallGraph(@RequestBody CallGraphRequest callGraphRequest) {
        if (callGraphRequest == null) {
            return CallGraphResponse.exceptionResponse("request is null.");
        }
        String reposURL = callGraphRequest.getRepositoryURL();
        String className = callGraphRequest.getClassName();
        String methodName = callGraphRequest.getMethodName();
        if (StringUtils.isEmpty(reposURL)
                || StringUtils.isEmpty(className)
                || StringUtils.isEmpty(methodName)) {
            return CallGraphResponse.exceptionResponse("request url or className or methodName is empty.");
        }

        return CallTrace.traceRequest(reposURL, "", className, methodName);
    }


}