package com.arextest.web.model.contract.contracts;

import lombok.Data;

import java.util.List;

/**
 * Created by rchen9 on 2023/3/14.
 */
@Data
public class QueryFullLinkInfoResponseType {
    List<FullLinkInfoItem> queryFullLinkInfoItemList;
}
