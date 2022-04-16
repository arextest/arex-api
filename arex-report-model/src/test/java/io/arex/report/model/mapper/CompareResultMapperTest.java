package io.arex.report.model.mapper;

import io.arex.report.model.api.contracts.common.CompareResult;
import io.arex.report.model.api.contracts.common.LogEntity;
import io.arex.report.model.dao.mongodb.ReplayCompareResultCollection;
import io.arex.report.model.dto.CompareResultDto;
import io.arex.report.model.enums.DiffResultCode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest(classes = CompareResultMapper.class)
@RunWith(SpringRunner.class)
public class CompareResultMapperTest {

    @Test
    public void testDaoFromDto() {
        CompareResultDto dto = new CompareResultDto();
        dto.setPlanId(1L);
        dto.setBaseMsg("baseMsg");
        dto.setPlanItemId(2L);
        dto.setReplayId("replayId");
        dto.setDiffResultCode(DiffResultCode.COMPARED_WITH_DIFFERENCE);
        dto.setOperationId(3L);
        dto.setTestMsg("testMsg");
        dto.setRecordId("recordId");
        dto.setOperationName("ron");
        dto.setCategoryName("SOA");
        dto.setServiceName("serviceName");
        ReplayCompareResultCollection dao = CompareResultMapper.INSTANCE.daoFromDto(dto);
        assertEquals("baseMsg", dao.getBaseMsg());
    }

    @Test
    public void testDtoFromDao() {
        ReplayCompareResultCollection dao = new ReplayCompareResultCollection();
        dao.setLogs("diffDetail");
        dao.setPlanId(1L);
        dao.setBaseMsg("baseMsg");
        dao.setPlanItemId(2L);
        dao.setReplayId("replayId");
        dao.setDiffResultCode(DiffResultCode.COMPARED_WITH_DIFFERENCE);
        dao.setOperationId(3L);
        dao.setTestMsg("testMsg");
        dao.setRecordId("recordId");
        dao.setOperationName("ron");
        dao.setCategoryName("SOA");
        dao.setServiceName("serviceName");
        dao.setLogs("KLUv/WAJAk0LAHYTRDFwwU0E4v+/fhxN/7mOHkRyZKswu32q3LSosnt9d21wULboxXXlKSVSCnV59vWvC0IPNwA6ADIAGjb/"
                + "OnZ91FB2RtWCKqPxr3/za9px5MMReMSBRSNKR0M6GBABQ8FwFDE6saFs3+hM8DsVmh8XBVC29fNBeMppM7+mAWW7NoEmt6SfWN"
                + "2a+XFQtr3mY/3UogJ2HAQAkaFYLhKWiwDKgAAVTCUi8zCBkQRf3dpNanZp2o+g7Dtxg78XtarLSphf0zE/B8p28KleTd/t51+/"
                + "NdOuW2xCfMpqM/PjopLudfx8sw9lV1eEshloVxOqMtoLB8qudjZQulXr5+NTBll4KI9M0+zXMT+ruiJP+YFL00IBIABWE8Znmf"
                + "HtcaXrg/oiPJTEEQgglJJDuMWyhP45kI8EANRBiubHGUZBSdRkgPkhcYMlKhjhrNJwGw3cyheibZ1Ygq2HivSugYwVBV0D5moQ"
                + "C5SNggI=");
        CompareResultDto dto = CompareResultMapper.INSTANCE.dtoFromDao(dao);
        assertEquals("replayId", dto.getReplayId());
        assertNotNull(dto.getLogs());
    }

    @Test
    public void testDtoFromContract() {
        CompareResult contract = new CompareResult();
        contract.setPlanId(1L);
        contract.setBaseMsg("baseMsg");
        contract.setPlanItemId(2L);
        contract.setReplayId("replayId");
        contract.setDiffResultCode(DiffResultCode.COMPARED_WITH_DIFFERENCE);
        contract.setOperationId(3L);
        contract.setTestMsg("testMsg");
        contract.setRecordId("recordId");
        contract.setOperationName("ron");
        contract.setCategoryName("SOA");
        contract.setServiceName("serviceName");
        CompareResultDto dto = CompareResultMapper.INSTANCE.dtoFromContract(contract);
        assertEquals("testMsg", dto.getTestMsg());
    }

    @Test
    public void testLogEntityConvert() {
        CompareResultDto dto = new CompareResultDto();
        dto.setDiffResultCode(DiffResultCode.COMPARED_WITH_DIFFERENCE);
        List<LogEntity> logs = new ArrayList<>();
        LogEntity log = new LogEntity();
        log.setBaseValue("a");
        log.setTestValue("B");
        logs.add(log);
        dto.setLogs(logs);
        ReplayCompareResultCollection dao = CompareResultMapper.INSTANCE.daoFromDto(dto);
        assertNotNull(dao);

        CompareResultDto dto_new = CompareResultMapper.INSTANCE.dtoFromDao(dao);
        assertNotNull(dto_new);
    }
}
