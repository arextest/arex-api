package com.arextest.web.api.service.controller;

import com.arextest.web.accurate.lib.DynamicTracing;
import com.arextest.web.accurate.lib.JCodeMethod;
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

    /**
     * 根据url和分支,拉取代码后,扫描,然后获取所有的Method,并转换到JCodeMethod,然后返回
     * @param request
     * @return
     */
    @PostMapping(value = "/analysis/scan", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Response scan(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("commit"))
            return Response.exceptionResponse("request is empty. ");

        try {
            javaProject.setupJavaProject(request.getRepositoryURL(),request.getBranch());
            javaProject.scanWholeProject();
            List<JCodeMethod> listMethods = javaProject.getJCodeMethods();
            Response response = new Response();
            response.setData(listMethods);
            response.setErrorCode(10000);
            return response;
        }catch (Exception e){
            LOGGER.error(e.toString());
            return Response.exceptionResponse(e.toString());
        }
    }

    /**
     * 提前准备好了Spring的Annotation,所有调用链
     * 老代码实现
     * @param request request info
     * @return
     */
    @PostMapping(value = "/analysis/spring", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public TracingResponse traceSpring(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("listCommit")) {
            return TracingResponse.exceptionResponse("git repository is empty or beginDate is empty. ");
        }

        try {
            javaProject.setupJavaProject(request.getRepositoryURL(), request.getBranch());
            javaProject.scanWholeProject();
            List<MethodTracing> result = javaProject.traceCallGraphInSpring();

            TracingResponse tracingResponse = new TracingResponse();
            tracingResponse.setStatusCode(10000);
            tracingResponse.setData(result);
            return tracingResponse;
        }catch (Exception e){
            LOGGER.error(e.toString());
            return TracingResponse.exceptionResponse(e.toString());
        }
    }

    /**
     * 列出有变更的类名:函数名
     * 必须是在newCommit和oldCommit之间的差异
     *
     * @param request 参数url和两个commit
     * @return
     */
    @PostMapping(value = "/analysis/DiffMethods", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public GitBasicResponse listDiffMethods(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("commit"))
            return GitBasicResponse.exceptionResponse("git repository url is empty");

        String url = request.getRepositoryURL();
        String branch = request.getBranch();
        String latestCommit = request.getNewCommit();
        String oldCommit = request.getOldCommit();
        return javaProject.DoListDiffMethods(url,latestCommit,oldCommit);
    }

    /**
     * 查询指定的函数其调用图 Call Graph
     * 静态代码分析, 查询调用链
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

        try {
            javaProject.setupJavaProject(reposURL, "");
            javaProject.scanWholeProject();
            javaProject.tra

            return CallTrace.traceRequest(reposURL, "", className, methodName);
        }catch (Exception e){
            LOGGER.info(e.toString());
            return CallGraphResponse.exceptionResponse(e.toString());
        }
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