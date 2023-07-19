package com.arextest.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
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
            return ResponseUtils.exceptionResponse("request is empty. ");

        try {
            javaProject.setupJavaProject(request.getRepositoryURL(),request.getBranch());
            javaProject.scanWholeProject();
            List<JCodeMethod> listMethods = javaProject.getJCodeMethods();
            return ResponseUtils.successResponse(listMethods);
        }catch (Exception e){
            LOGGER.error(e.toString());
            return ResponseUtils.exceptionResponse(e.toString());
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
    public Response listDiffMethods(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("commit"))
            return ResponseUtils.exceptionResponse("git repository url is empty");

        String url = request.getRepositoryURL();
        String branch = request.getBranch();
        String latestCommit = request.getNewCommit();
        String oldCommit = request.getOldCommit();

        javaProject.setupJavaProject(url,branch);
        List<JCodeMethod> result = javaProject.scanCodeDiffMethods(latestCommit, oldCommit);

        if (result == null || result.size() == 0) {
            return ResponseUtils.errorResponse("no diff code", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }

        return ResponseUtils.successResponse(result);
    }

    /**
     * 提前准备好了Spring的Annotation,查询所有调用链
     * 老代码实现
     * @param request request info
     * @return
     */
    @PostMapping(value = "/analysis/static/spring", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Response traceSpring(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("listCommit")) {
            return ResponseUtils.errorResponse("git repository is empty or beginDate is empty. ", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }

        try {
            javaProject.setupJavaProject(request.getRepositoryURL(), request.getBranch());
            javaProject.scanWholeProject();
            List<MethodTracing> result = javaProject.searchCallGraphBasedSpringAnnotation();

            TracingResponse tracingResponse = new TracingResponse();
            tracingResponse.setStatusCode(20000);
            tracingResponse.setData(result);
            return ResponseUtils.successResponse(tracingResponse);
        }catch (Exception e){
            LOGGER.error(e.toString());
            return ResponseUtils.exceptionResponse(e.toString());
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
    public Response queryCallGraph(@RequestBody CallGraphRequest callGraphRequest) {
        String reposURL = callGraphRequest.getRepositoryURL();
        String className = callGraphRequest.getClassName();
        String methodName = callGraphRequest.getMethodName();
        if (StringUtils.isEmpty(reposURL)
                || StringUtils.isEmpty(className)
                || StringUtils.isEmpty(methodName)) {
            return ResponseUtils.errorResponse("request url or className or methodName is empty.", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }

        try {
            javaProject.setupJavaProject(reposURL, callGraphRequest.getBranch());
            javaProject.scanWholeProject();
            List<MethodTracing> result = javaProject.findMethodCallGraph(className, methodName);

            return ResponseUtils.successResponse(result);
        }catch (Exception e){
            LOGGER.info(e.toString());
            return ResponseUtils.exceptionResponse(e.toString());
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
            return ResponseUtils.exceptionResponse(" url cannot be empty");
        }

        return ResponseUtils.successResponse(javaProject.DoGitClone(baseRequest));
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
            return ResponseUtils.exceptionResponse("url is empty");
        }

        return ResponseUtils.successResponse(javaProject.DoCleanProject(baseRequest.getRepositoryURL()));
    }

    /**
     * 获取所有的commits
     * @param request url
     * @return
     */
    @PostMapping(value = "/git/commits", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Response listCommits(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("commits"))
            return ResponseUtils.exceptionResponse("git repository url is empty");

        return ResponseUtils.successResponse(javaProject.DoListCommits(request));
    }
}