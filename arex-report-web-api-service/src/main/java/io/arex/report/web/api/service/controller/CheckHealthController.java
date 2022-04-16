package io.arex.report.web.api.service.controller;

import io.arex.common.model.response.Response;
import io.arex.common.utils.ResponseUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/vi/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CheckHealthController {

    
    @GetMapping("/health")
    @ResponseBody
    public Response checkHealth(){
        return ResponseUtils.successResponse(true);
    }
}
