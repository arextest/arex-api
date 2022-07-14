package com.arextest.report.core.business;

import com.arextest.report.core.repository.*;
import com.arextest.report.model.api.contracts.manualreport.*;
import com.arextest.report.model.dto.filesystem.FSCaseDto;
import com.arextest.report.model.dto.filesystem.FSInterfaceDto;
import com.arextest.report.model.dto.filesystem.FSNodeDto;
import com.arextest.report.model.dto.filesystem.FSTreeDto;
import com.arextest.report.model.dto.manualreport.ManualReportCaseDto;
import com.arextest.report.model.dto.manualreport.ManualReportPlanDto;
import com.arextest.report.model.dto.manualreport.ManualReportPlanItemDto;
import com.arextest.report.model.dto.manualreport.SaveManualReportCaseDto;
import com.arextest.report.model.mapper.ManualReportCaseMapper;
import com.arextest.report.model.mapper.ManualReportPlanItemMapper;
import com.arextest.report.model.mapper.ManualReportPlanMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ManualReportService {

    @Resource
    private FSCaseRepository fsCaseRepository;
    @Resource
    private FSInterfaceRepository fsInterfaceRepository;
    @Resource
    private FSTreeRepository fsTreeRepository;
    @Resource
    private ManualReportPlanRepository manualReportPlanRepository;
    @Resource
    private ManualReportPlanItemRepository manualReportPlanItemRepository;
    @Resource
    private ManualReportCaseRepository manualReportCaseRepository;

    public InitManualReportResponseType initManualReport(InitManualReportRequestType request) {
        InitManualReportResponseType response = new InitManualReportResponseType();
        response.setInterfaces(new ArrayList<>());

        List<FSCaseDto> cases = fsCaseRepository.queryCases(request.getCaseIds());
        Map<String, List<FSCaseDto>> caseMap = cases.stream().collect(Collectors.groupingBy(FSCaseDto::getParentId));
        Set<String> interfaceIds = cases.stream().map(c -> c.getParentId()).collect(Collectors.toSet());
        Set<String> ids = cases.stream().map(c -> c.getId()).collect(Collectors.toSet());
        ids.addAll(interfaceIds);
        Map<String, String> idNameMap = getNames(request.getWorkspaceId(), ids);

        // init manualPlan
        ManualReportPlanDto planDto = ManualReportPlanMapper.INSTANCE.dtoFromContract(request);
        planDto = manualReportPlanRepository.initManualReportPlan(planDto);
        response.setReportId(planDto.getId());

        // init manualPlanItems
        List<FSInterfaceDto> interfaceDtos = fsInterfaceRepository.queryInterfaces(interfaceIds);
        for (FSInterfaceDto interfaceDto : interfaceDtos) {
            List<FSCaseDto> filterCases = caseMap.get(interfaceDto.getId());
            if (filterCases == null || filterCases.size() == 0) {
                continue;
            }
            ManualReportPlanItemDto planItemDto =
                    ManualReportPlanItemMapper.INSTANCE.dtoFromFsInterfaceDto(interfaceDto);
            planItemDto.setPlanId(planDto.getId());
            planItemDto.setInterfaceName(idNameMap.get(planItemDto.getId()));
            planItemDto.setId(null);
            planItemDto = manualReportPlanItemRepository.initManualReportPlanItems(planItemDto);

            // init manualReportCases
            List<ManualReportCaseDto> reportCaseDtos = ManualReportCaseMapper.INSTANCE.dtoFromFsCaseDto(filterCases);
            ReportInterfaceType interfaceType = ManualReportPlanItemMapper.INSTANCE.contractFromDto(planItemDto);
            interfaceType.setReportCases(new ArrayList<>(reportCaseDtos.size()));
            for (ManualReportCaseDto reportCaseDto : reportCaseDtos) {
                reportCaseDto.setPlanItemId(planItemDto.getId());
                reportCaseDto.setCaseName(idNameMap.get(reportCaseDto.getId()));
                reportCaseDto.setId(null);
            }
            reportCaseDtos = manualReportCaseRepository.initManualReportCases(reportCaseDtos);

            for (ManualReportCaseDto reportCaseDto : reportCaseDtos) {
                ReportCaseIdNameType reportCase = new ReportCaseIdNameType();
                reportCase.setId(reportCaseDto.getId());
                reportCase.setCaseName(reportCaseDto.getCaseName());
                interfaceType.getReportCases().add(reportCase);
            }
            response.getInterfaces().add(interfaceType);
        }
        return response;
    }

    public List<ReportCaseType> queryReportCases(QueryReportCasesRequestType request) {
        if (request.getReportCaseIds() == null || request.getReportCaseIds().size() == 0) {
            return new ArrayList<>();
        }
        List<ManualReportCaseDto> dtos = manualReportCaseRepository.queryManualReportCases(request.getReportCaseIds());
        return dtos.stream().map(ManualReportCaseMapper.INSTANCE::contractFromDto).collect(Collectors.toList());
    }

    public boolean saveManualReportCaseResults(List<SaveManualReportCaseDto> caseDtos) {
        boolean result = true;
        for (SaveManualReportCaseDto caseDto : caseDtos) {
            result &= manualReportCaseRepository.saveManualReportCaseResult(caseDto);
        }
        return result;
    }

    private Map<String, String> getNames(String workspaceId, Set<String> infoIds) {
        FSTreeDto treeDto = fsTreeRepository.queryFSTreeById(workspaceId);
        if (treeDto == null || treeDto.getRoots() == null) {
            return new HashMap<>();
        }
        Map<String, String> idNameMap = new HashMap<>();
        Queue<FSNodeDto> nodeQueue = new ArrayDeque<>();
        nodeQueue.addAll(treeDto.getRoots());

        while (!nodeQueue.isEmpty()) {
            FSNodeDto nodeDto = nodeQueue.poll();
            if (infoIds.contains(nodeDto.getInfoId())) {
                idNameMap.put(nodeDto.getInfoId(), nodeDto.getNodeName());
            }
            if (nodeDto.getChildren() != null && nodeDto.getChildren().size() > 0) {
                nodeQueue.addAll(nodeDto.getChildren());
            }
        }
        return idNameMap;
    }
}
