package com.arextest.accurate.controller;

import com.arextest.accurate.lib.JavaProject;
import com.arextest.accurate.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@Scope("prototype")
@RequestMapping("/api/git")
@CrossOrigin(origins = "*", maxAge = 180)
public class GitCodeController {

    @PostMapping(value = "/clone", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Response gitClone(@RequestBody GitBasicRequest baseRequest) {
        if (baseRequest.requestNotPrepared("clone")) {
            return Response.exceptionResponse(" url cannot be empty");
        }

        return (new JavaProject()).DoGitClone(baseRequest);
    }

    @PostMapping(value = "/clean", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Response gitClean(@RequestBody GitBasicRequest baseRequest) {
        if (baseRequest.requestNotPrepared("clean")) {
            return Response.exceptionResponse("url is empty");
        }

        JavaProject codeProject = new JavaProject();
        return codeProject.DoCleanProject(baseRequest.getRepositoryURL());
    }

    @PostMapping(value = "/commits", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public CommitsResponse listCommits(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("commits"))
            return CommitsResponse.exceptionResponse("git repository url is empty");

        JavaProject project = new JavaProject();
        return project.DoListCommits(request);
    }

    /**
     * 列出有变更的类名:函数名
     * 必须是在newCommit和oldCommit之间的差异
     * @param request
     * @return
     */
    @PostMapping(value = "/diffMethods", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public GitBasicResponse listDiffMethods(@RequestBody GitBasicRequest request) {
        if (request.requestNotPrepared("commit"))
            return GitBasicResponse.exceptionResponse("git repository url is empty");

        JavaProject project = new JavaProject();
        return project.DoListDiffMethods(request);
    }



}