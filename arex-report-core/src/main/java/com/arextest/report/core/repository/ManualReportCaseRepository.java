package com.arextest.report.core.repository;


import com.arextest.report.model.dto.manualreport.ManualReportCaseDto;
import com.arextest.report.model.dto.manualreport.SaveManualReportCaseDto;

import java.util.List;

public interface ManualReportCaseRepository extends RepositoryProvider {
    List<ManualReportCaseDto> initManualReportCases(List<ManualReportCaseDto> caseDtos);

    List<ManualReportCaseDto> queryManualReportCases(List<String> ids);

    boolean saveManualReportCaseResult(SaveManualReportCaseDto caseDto);
}
