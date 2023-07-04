package com.arextest.web.api.service.controller;

import com.arextest.accurate.lib.JavaProject;
import com.arextest.accurate.model.*;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.webhook.EventMergeRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@Scope("prototype")
@RequestMapping("/api/webhook/")
@CrossOrigin(origins = "*", maxAge = 180)
public class WebhookController {

    @PostMapping(value = "/gitlab", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Response gitLabWebHookOfGitlab(@RequestBody EventMergeRequest eventMerge) {
        if (!eventMerge.getAction().equals("approved")){
            return Response.successResponse();
        }

        try {
            return (new JavaProject()).DoEventMergeRequestOfGitlab(eventMerge);
        }catch (Exception e){
            return Response.exceptionResponse(e.toString());
        }
    }

    @PostMapping(value = "/github", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Response gitLabWebHookOfGitHub(@RequestBody EventMergeRequest eventMerge) {
        // TODO
        return Response.successResponse();
    }



}