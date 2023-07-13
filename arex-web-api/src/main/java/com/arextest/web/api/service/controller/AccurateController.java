package com.arextest.web.api.service.controller;

import com.arextest.web.accurate.biz.CallTrace;
import com.arextest.web.accurate.biz.CodeAnalysis;
import com.arextest.web.accurate.lib.DynamicTracing;
import com.arextest.web.accurate.lib.JavaProject;
import com.arextest.web.accurate.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Controller
@Scope("prototype")
@RequestMapping("/api/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccurateController {
    @Resource
    private JavaProject javaProject;

    @PostMapping(value = "/analysis/scan", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Response scan(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("commit"))
            return Response.exceptionResponse("request is empty. ");

        JavaProject jp = CodeAnalysis.ScanJava(javaProject,request.getRepositoryURL(), "");
        if (jp == null)
            return Response.exceptionResponse("init Project failed");

        return jp.DoCodeScan(request);
    }


    @PostMapping(value = "/analysis/spring", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public TracingResponse traceSpring(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("listCommit")) {
            return TracingResponse.exceptionResponse("git repository is empty or beginDate is empty. ");
        }

        JavaProject jp = CodeAnalysis.ScanJava(javaProject, request.getRepositoryURL(), request.getBranch());
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
     * 列出有变更的类名:函数名
     * 必须是在newCommit和oldCommit之间的差异
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/analysis/DiffMethods", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public GitBasicResponse listDiffMethods(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("commit"))
            return GitBasicResponse.exceptionResponse("git repository url is empty");

        return javaProject.DoListDiffMethods(request);
    }

    /**
     * 查询指定的函数其调用图 Call Graph
     *
     * @param callGraphRequest 请求参数
     * @return List<String>目前只是字符串List
     */
    @PostMapping("/analysis/staticTrace")
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

    @PostMapping(value = "/trace/dynamic", produces = "application/json; charset=UTF-8")
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

    @PostMapping(value = "/git/clone", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Response gitClone(@RequestBody GitBasicRequest baseRequest) {
        if (baseRequest.requestNotPrepared("clone")) {
            return Response.exceptionResponse(" url cannot be empty");
        }

        return javaProject.DoGitClone(baseRequest);
    }

    @PostMapping(value = "/git/clean", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Response gitClean(@RequestBody GitBasicRequest baseRequest) {
        if (baseRequest.requestNotPrepared("clean")) {
            return Response.exceptionResponse("url is empty");
        }

        return javaProject.DoCleanProject(baseRequest.getRepositoryURL());
    }

    @PostMapping(value = "/git/commits", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public CommitsResponse listCommits(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("commits"))
            return CommitsResponse.exceptionResponse("git repository url is empty");

        return javaProject.DoListCommits(request);
    }


}