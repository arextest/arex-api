package com.arextest.web.api.service.controller;

import com.arextest.web.accurate.lib.JavaProject;
import com.arextest.web.accurate.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Controller
@Scope("prototype")
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 180)
public class WebhookController {
    @Resource
    JavaProject javaProject;

    @PostMapping(value = "/gitlab/webhook", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Response gitLabWebHookOfGitlab(@RequestBody Map<String, Object> payload) {
        String eventType = (String) payload.get("object_kind");
        switch (eventType) {
            case "merge_request":
                Map<String, Object> objectAttributes = (Map<String, Object>) payload.get("object_attributes");
                String sourceBranch = (String) objectAttributes.get("source_branch");
                String targetBranch = (String) objectAttributes.get("target_branch");
                String commitID = (String) objectAttributes.get("merge_commit_sha");
                String state = (String) objectAttributes.get("state");
                String action = (String) objectAttributes.get("action");
                if (!"opened".equals(state)
                        || !"approval".equals(action)
                        || !"release".equals(targetBranch)) {
                    return Response.successResponse();                      // 忽略
                }

                Map<String, Object> projects = (Map<String, Object>) payload.get("project");
                String gitURL = (String) projects.get("http_url");

                try {
                    return javaProject.DoEventMergeRequestOfGitlab(gitURL, commitID, targetBranch);
                } catch (Exception e) {
                    return Response.exceptionResponse(e.toString());
                }
            case "push":
            default:
                return Response.successResponse();
        }

    }

    @PostMapping(value = "/github/webhook/", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Response gitLabWebHookOfGitHub(@RequestBody Map<String, Object> payload) {
        // TODO
        return Response.successResponse();
    }


}