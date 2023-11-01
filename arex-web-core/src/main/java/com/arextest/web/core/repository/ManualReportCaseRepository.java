package com.arextest.web.core.repository;

import java.util.List;

import com.arextest.web.model.dto.manualreport.ManualReportCaseDto;
import com.arextest.web.model.dto.manualreport.SaveManualReportCaseDto;

public interface ManualReportCaseRepository extends RepositoryProvider {
    List<ManualReportCaseDto> initManualReportCases(List<ManualReportCaseDto> caseDtos);

    List<ManualReportCaseDto> queryManualReportCases(List<String> ids);

    boolean saveManualReportCaseResult(SaveManualReportCaseDto caseDto);
}
