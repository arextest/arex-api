package com.arextest.web.core.business.preprocess;

import com.arextest.web.core.repository.MessagePreprocessRepository;
import com.arextest.web.model.dto.MessagePreprocessDto;
import com.github.benmanes.caffeine.cache.CacheLoader;
import java.util.HashMap;
import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

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
            tmp.getChildren()
                .put(pathNode, new PreprocessTreeNode(key, true, System.currentTimeMillis()));
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
