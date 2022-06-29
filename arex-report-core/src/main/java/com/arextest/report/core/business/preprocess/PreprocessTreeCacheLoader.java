package com.arextest.report.core.business.preprocess;

import com.arextest.report.core.repository.MessagePreprocessRepository;
import com.arextest.report.model.dto.MessagePreprocessDto;
import com.github.benmanes.caffeine.cache.CacheLoader;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
public class PreprocessTreeCacheLoader implements CacheLoader<String, PreprocessTreeNode> {
    @Resource
    private MessagePreprocessRepository messagePreprocessRepository;

    @Override
    public PreprocessTreeNode load(String key) {
        List<MessagePreprocessDto> dtos = messagePreprocessRepository.queryMessagesByKey(key);
        if (dtos == null || dtos.size() == 0) {
            return null;
        }
        PreprocessTreeNode root = new PreprocessTreeNode();
        for (MessagePreprocessDto dto : dtos) {
            String[] pathArray = dto.getPath().split("/");
            PreprocessTreeNode tmp = root;
            for (int i = 0; i < pathArray.length; i++) {
                String pathNode = pathArray[i];
                if (tmp.getChildren() == null) {
                    tmp.setChildren(new HashMap<>());
                }
                if (!tmp.getChildren().containsKey(pathNode)) {
                    if (i == pathArray.length - 1) {
                        tmp.getChildren().put(pathNode, new PreprocessTreeNode(key,
                                true, System.currentTimeMillis()));
                    } else {
                        tmp.getChildren().put(pathNode, new PreprocessTreeNode());
                    }
                }
                tmp = tmp.getChildren().get(pathNode);
            }
        }
        return root;
    }
}
