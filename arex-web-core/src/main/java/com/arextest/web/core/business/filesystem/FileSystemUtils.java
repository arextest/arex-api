package com.arextest.web.core.business.filesystem;

import com.arextest.web.common.Tuple;
import com.arextest.web.model.dto.filesystem.FSNodeDto;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

/**
 * @author b_yu
 * @since 2022/10/8
 */
@Component
public class FileSystemUtils {
    public FSNodeDto findByPath(List<FSNodeDto> list, String[] pathArr) {
        Tuple<Integer, FSNodeDto> result = findByPathWithIndex(list, pathArr);
        if (result == null) {
            return null;
        }
        return result.y;
    }

    public Tuple<Integer, FSNodeDto> findByPathWithIndex(List<FSNodeDto> list, String[] pathArr) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<FSNodeDto> tmp = list;
        for (int i = 0; i < pathArr.length - 1; i++) {
            String pathNode = pathArr[i];
            if (tmp == null || tmp.size() == 0) {
                return null;
            }
            Tuple<Integer, FSNodeDto> find = findByInfoIdWithIndex(tmp, pathNode);
            if (find == null) {
                return null;
            }
            tmp = find.y.getChildren();
        }
        String last = pathArr[pathArr.length - 1];
        return findByInfoIdWithIndex(tmp, last);
    }

    public FSNodeDto findByInfoId(List<FSNodeDto> list, String infoId) {
        Tuple<Integer, FSNodeDto> result = findByInfoIdWithIndex(list, infoId);
        if (result == null) {
            return null;
        }
        return result.y;
    }

    public Tuple<Integer, FSNodeDto> findByInfoIdWithIndex(List<FSNodeDto> list, String infoId) {
        if (list == null || list.size() == 0) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            FSNodeDto dto = list.get(i);
            if (Objects.equals(dto.getInfoId(), infoId)) {
                return new Tuple<>(i, dto);
            }
        }
        return null;
    }

    public FSNodeDto deepFindByInfoId(List<FSNodeDto> list, String infoId) {
        Queue<FSNodeDto> queue = new ArrayDeque<>(list);
        while (!queue.isEmpty()) {
            FSNodeDto node = queue.poll();
            if (Objects.equals(node.getInfoId(), infoId)) {
                return node;
            }
            if (node.getChildren() != null && node.getChildren().size() > 0) {
                queue.addAll(node.getChildren());
            }
        }
        return null;
    }
}
