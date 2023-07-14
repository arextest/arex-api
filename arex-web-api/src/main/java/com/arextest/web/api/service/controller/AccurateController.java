package com.arextest.web.api.service.controller;

import com.arextest.web.accurate.lib.JCodeMethod;
import com.arextest.web.accurate.lib.JavaProject;
import com.arextest.web.accurate.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping(value = "/scan/methods", produces = "application/json; charset=UTF-8")
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
            response.setErrorCode(20000);
            return response;
        }catch (Exception e){
            LOGGER.error(e.toString());
            return Response.exceptionResponse(e.toString());
        }
    }

    /**
     * 列出有变更的类名:函数名
     * 必须是在newCommit和oldCommit之间的差异
     * 测试了两个,不太对
     *
     * @param request 参数url和两个commit
     * @return
     */
    @PostMapping(value = "/scan/changed-methods", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public GitBasicResponse listDiffMethods(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("commit"))
            return GitBasicResponse.exceptionResponse("git repository url is empty");

        String url = request.getRepositoryURL();
        String branch = request.getBranch();
        String latestCommit = request.getNewCommit();
        String oldCommit = request.getOldCommit();

        javaProject.setupJavaProject(url,branch);
        List<JCodeMethod> result = javaProject.scanCodeDiffMethods(latestCommit, oldCommit);

        GitBasicResponse response = new GitBasicResponse();
        response.setNewCommit(latestCommit);
        response.setOldCommit(oldCommit);
        if (result == null || result.size() == 0) {
            response.setErrorCode(10001);
            response.setResult("No different code between two commit.");
            return response;
        }

        response.setErrorCode(20000);
        response.setResult("success diff code");
        response.setData(result);
        return response;
    }

    /**
     * 提前准备好了Spring的Annotation,查询所有调用链
     * 老代码实现
     * @param request request info
     * @return
     */
    @PostMapping(value = "/analysis/static/spring", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public TracingResponse traceSpring(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("listCommit")) {
            return TracingResponse.exceptionResponse("git repository is empty or beginDate is empty. ");
        }

        try {
            javaProject.setupJavaProject(request.getRepositoryURL(), request.getBranch());
            javaProject.scanWholeProject();
            List<MethodTracing> result = javaProject.searchCallGraphBasedSpringAnnotation();

            TracingResponse tracingResponse = new TracingResponse();
            tracingResponse.setStatusCode(20000);
            tracingResponse.setData(result);
            return tracingResponse;
        }catch (Exception e){
            LOGGER.error(e.toString());
            return TracingResponse.exceptionResponse(e.toString());
        }
    }



    /**
     * 查询指定的函数其调用图 Call Graph
     * 静态代码分析, 查询调用链
     * 会出现单步执行是OK的,但常规运行的时候抛异常,问题未定位
     *
     * @param callGraphRequest 请求参数
     * @return List<String>目前只是字符串List
     */
    @PostMapping("/analysis/static/method")
    @ResponseBody
    public CallGraphResponse queryCallGraph(@RequestBody CallGraphRequest callGraphRequest) {
        String reposURL = callGraphRequest.getRepositoryURL();
        String className = callGraphRequest.getClassName();
        String methodName = callGraphRequest.getMethodName();
        if (StringUtils.isEmpty(reposURL)
                || StringUtils.isEmpty(className)
                || StringUtils.isEmpty(methodName)) {
            return CallGraphResponse.exceptionResponse("request url or className or methodName is empty.");
        }

        try {
            javaProject.setupJavaProject(reposURL, callGraphRequest.getBranch());
            javaProject.scanWholeProject();
            List<MethodTracing> result = javaProject.findMethodCallGraph(className, methodName);

            CallGraphResponse response = new CallGraphResponse();
            response.setStacks(result);
            response.setErrorCode(20000); //success;
            return response;
        }catch (Exception e){
            LOGGER.info(e.toString());
            return CallGraphResponse.exceptionResponse(e.toString());
        }
    }

    /**
     * git clone 命令
     * 不能重复的clean clone操作, 会失败的, 提示目录有问题
     * @param baseRequest url
     * @return
     *
     */
    @PostMapping(value = "/git/clone", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Response gitClone(@RequestBody GitBasicRequest baseRequest) {
        if (baseRequest.requestNotPrepared("clone")) {
            return Response.exceptionResponse(" url cannot be empty");
        }

        return javaProject.DoGitClone(baseRequest);
    }

    /**
     * 清除代码库本地的clone
     * @param baseRequest url
     * @return
     */
    @PostMapping(value = "/git/clean", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Response gitClean(@RequestBody GitBasicRequest baseRequest) {
        if (baseRequest.requestNotPrepared("clean")) {
            return Response.exceptionResponse("url is empty");
        }

        return javaProject.DoCleanProject(baseRequest.getRepositoryURL());
    }

    /**
     * 获取所有的commits
     * @param request url
     * @return
     */
    @PostMapping(value = "/git/commits", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public CommitsResponse listCommits(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("commits"))
            return CommitsResponse.exceptionResponse("git repository url is empty");

        return javaProject.DoListCommits(request);
    }


}