package com.arextest.web.model.dao.mongodb.entity;

import com.arextest.web.model.dao.mongodb.ModelBase;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Date;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Getter
@Setter
public abstract class AbstractComparisonDetails extends ModelBase {

    @NonNull
    private String appId;

    private String operationId;

    private int expirationType;
    @NonNull
    private Date expirationDate;
}
