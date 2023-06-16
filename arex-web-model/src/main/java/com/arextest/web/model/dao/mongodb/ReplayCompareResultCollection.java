package com.arextest.web.model.dao.mongodb;

import com.arextest.web.model.dao.mongodb.entity.MsgInfoDao;
import com.arextest.web.model.dto.MsgInfoDto;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Data
@Document(collection = "ReplayCompareResult")
public class ReplayCompareResultCollection extends ModelBase {
    
    private String planId;
    
    private String operationId;
    
    private String serviceName;
    
    private String categoryName;
    
    private String operationName;
    
    private String replayId;
    
    private String recordId;

    private long recordTime;

    private long replayTime;

    private String instanceId;

    private String baseMsg;
    
    private String testMsg;
    
    private String logs;
    // planItemId
    private String planItemId;
    
    private int diffResultCode;

    private MsgInfoDao msgInfo;

    private Date dataCreateTime;
}
