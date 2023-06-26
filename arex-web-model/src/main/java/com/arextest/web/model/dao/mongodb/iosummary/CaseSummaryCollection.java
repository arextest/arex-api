package com.arextest.web.model.dao.mongodb.iosummary;

import com.arextest.web.model.dao.mongodb.ModelBase;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * Created by rchen9 on 2023/2/28.
 */
@Data
@NoArgsConstructor
@Document(collection = "CaseSummary")
public class CaseSummaryCollection extends ModelBase {

    private int code;
    private String recordId;
    private String replayId;
    private String planId;
    private String planItemId;

    private List<DiffDetailDao> diffs;

    private Date dataCreateTime;
}
