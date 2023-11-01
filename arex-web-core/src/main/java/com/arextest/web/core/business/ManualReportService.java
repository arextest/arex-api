package com.arextest.web.core.business;

import com.arextest.web.core.repository.FSCaseRepository;
import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.core.repository.FSTreeRepository;
import com.arextest.web.core.repository.ManualReportCaseRepository;
import com.arextest.web.core.repository.ManualReportPlanItemRepository;
import com.arextest.web.core.repository.ManualReportPlanRepository;
import com.arextest.web.model.contract.contracts.manualreport.InitManualReportRequestType;
import com.arextest.web.model.contract.contracts.manualreport.InitManualReportResponseType;
import com.arextest.web.model.contract.contracts.manualreport.QueryReportCasesRequestType;
import com.arextest.web.model.contract.contracts.manualreport.ReportCaseIdNameType;
import com.arextest.web.model.contract.contracts.manualreport.ReportCaseType;
import com.arextest.web.model.contract.contracts.manualreport.ReportInterfaceType;
import com.arextest.web.model.dto.filesystem.FSCaseDto;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import com.arextest.web.model.dto.filesystem.FSItemDto;
import com.arextest.web.model.dto.filesystem.FSNodeDto;
import com.arextest.web.model.dto.filesystem.FSTreeDto;
import com.arextest.web.model.dto.manualreport.ManualReportCaseDto;
import com.arextest.web.model.dto.manualreport.ManualReportPlanDto;
import com.arextest.web.model.dto.manualreport.ManualReportPlanItemDto;
import com.arextest.web.model.dto.manualreport.SaveManualReportCaseDto;
import com.arextest.web.model.mapper.ManualReportCaseMapper;
import com.arextest.web.model.mapper.ManualReportPlanItemMapper;
import com.arextest.web.model.mapper.ManualReportPlanMapper;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

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

    List<FSItemDto> cases = fsCaseRepository.queryCases(request.getCaseIds(), false);
    Map<String, List<FSCaseDto>> caseMap =
        cases.stream().map(c -> (FSCaseDto) c)
            .collect(Collectors.groupingBy(FSCaseDto::getParentId));
    Set<String> interfaceIds = caseMap.keySet();
    Set<String> ids = cases.stream().map(c -> c.getId()).collect(Collectors.toSet());
    ids.addAll(interfaceIds);
    Map<String, String> idNameMap = getNames(request.getWorkspaceId(), ids);

    // init manualPlan
    ManualReportPlanDto planDto = ManualReportPlanMapper.INSTANCE.dtoFromContract(request);
    planDto = manualReportPlanRepository.initManualReportPlan(planDto);
    response.setReportId(planDto.getId());

    // init manualPlanItems
    List<FSItemDto> interfaceDtos = fsInterfaceRepository.queryInterfaces(interfaceIds);
    for (FSItemDto interfaceDto : interfaceDtos) {
      List<FSCaseDto> filterCases = caseMap.get(interfaceDto.getId());
      if (filterCases == null || filterCases.size() == 0) {
        continue;
      }
      ManualReportPlanItemDto planItemDto =
          ManualReportPlanItemMapper.INSTANCE.dtoFromFsInterfaceDto((FSInterfaceDto) interfaceDto);
      planItemDto.setPlanId(planDto.getId());
      planItemDto.setInterfaceName(idNameMap.get(planItemDto.getId()));
      planItemDto.setId(null);
      planItemDto = manualReportPlanItemRepository.initManualReportPlanItems(planItemDto);

      // init manualReportCases
      List<ManualReportCaseDto> reportCaseDtos = ManualReportCaseMapper.INSTANCE.dtoFromFsCaseDto(
          filterCases);
      ReportInterfaceType interfaceType = ManualReportPlanItemMapper.INSTANCE.contractFromDto(
          planItemDto);
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
    List<ManualReportCaseDto> dtos = manualReportCaseRepository.queryManualReportCases(
        request.getReportCaseIds());
    return dtos.stream().map(ManualReportCaseMapper.INSTANCE::contractFromDto)
        .collect(Collectors.toList());
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
