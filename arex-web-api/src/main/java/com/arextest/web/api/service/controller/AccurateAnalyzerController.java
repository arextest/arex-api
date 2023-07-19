package com.arextest.web.api.service.controller;

import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.accurate.model.BaseDiffReq;
import com.arextest.web.accurate.model.BaseDiffRes;
import com.arextest.web.accurate.service.MethodDiffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.arextest.common.model.response.Response;
import javax.annotation.Resource;

@Slf4j
@Controller
@RequestMapping("/api/accurate/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccurateAnalyzerController {
    @Resource
    MethodDiffService methodDiffService;

    @PostMapping(value = "/scan/listDiffMethods", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Response listDiffMethods(@RequestBody BaseDiffReq request) {
        BaseDiffRes res = new BaseDiffRes();
        res.setOldCommitSha(request.getOldCommitSha());
        res.setNewCommitSha(request.getNewCommitSha());
        res.setRepoPath(request.getRepoPath());
        res.setModifiedMethods(methodDiffService.listDiffMethods(request));
        return ResponseUtils.successResponse(res);
    }
}