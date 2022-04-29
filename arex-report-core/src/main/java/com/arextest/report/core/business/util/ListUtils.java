package com.arextest.report.core.business.util;

import com.arextest.report.model.api.contracts.common.NodeEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class ListUtils {

    public static final String POINT = ".";

    public static String convertPathToStringForShow(List<NodeEntity> nodes) {
        if (nodes == null) {
            return null;
        }
        StringBuilder path = new StringBuilder();
        for (int i = 0; i < nodes.size(); i++) {
            String suffix = (i == nodes.size() - 1) ? "" : POINT;
            NodeEntity no = nodes.get(i);
            if (!StringUtils.isEmpty(no.getNodeName())) {
                path.append(no.getNodeName() + suffix);
            } else {
                path.deleteCharAt(path.length() - 1);
                path.append("[").append(no.getIndex()).append("]").append(suffix);
            }
        }
        return path.toString();
    }

    
    public static void removeLast(List<?> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        list.remove(list.size() - 1);
    }

}
