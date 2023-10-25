package com.arextest.web.model.contract.contracts;

import java.util.List;

import lombok.Data;

/**
 * Created by rchen9 on 2023/3/14.
 */
@Data
public class QueryFullLinkInfoResponseType {
    FullLinkInfoItem entrance;
    List<FullLinkInfoItem> infoItemList;
}
