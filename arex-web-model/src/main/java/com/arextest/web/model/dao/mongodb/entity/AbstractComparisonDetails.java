package com.arextest.web.model.dao.mongodb.entity;

import java.util.Date;

import com.arextest.web.model.dao.mongodb.ModelBase;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Getter
@Setter
@FieldNameConstants
public abstract class AbstractComparisonDetails extends ModelBase {

    private String appId;

    private String operationId;

    private int expirationType;
    @NonNull
    private Date expirationDate;

    private int compareConfigType;

    private String fsInterfaceId;

    private String dependencyId;
}
