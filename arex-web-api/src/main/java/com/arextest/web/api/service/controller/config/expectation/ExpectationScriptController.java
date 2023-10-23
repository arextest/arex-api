package com.arextest.web.api.service.controller.config.expectation;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.repository.expectation.ExpectationScriptRepository;
import com.arextest.web.model.contract.contracts.config.expectation.ExpectationScriptDeleteRequest;
import com.arextest.web.model.contract.contracts.config.expectation.ExpectationScriptModel;
import com.arextest.web.model.contract.contracts.config.expectation.ExpectationScriptQueryRequest;
import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config/expectation")
public class ExpectationScriptController {
    private final ExpectationScriptRepository repository;

    public ExpectationScriptController(ExpectationScriptRepository repository) {
        this.repository = repository;
    }

    @RequestMapping("/query")
    public Response query(@Valid @RequestBody ExpectationScriptQueryRequest request) {
        List<ExpectationScriptModel> list = repository.query(request);
        return ResponseUtils.successResponse(list);
    }

    @RequestMapping("/save")
    public Response save(@Valid @RequestBody ExpectationScriptModel request) {
        boolean result = repository.save(request);
        if (!result) {
            return ResponseUtils.errorResponse("save failed", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
        return ResponseUtils.successResponse(true);
    }

    @RequestMapping("/delete")
    public Response delete(@Valid @RequestBody ExpectationScriptDeleteRequest request) {
        boolean result = repository.delete(request);
        if (!result) {
            return ResponseUtils.errorResponse("delete failed", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
        return ResponseUtils.successResponse(true);
    }
}
