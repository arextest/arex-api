package com.arextest.web.core.business.preprocess;

import java.util.Map;
import lombok.Data;

@Data
public class PreprocessTreeNode {

  String name;
  Boolean isLeaf;
  Long firstAppearTime;
  Map<String, PreprocessTreeNode> children;

  public PreprocessTreeNode() {
  }

  public PreprocessTreeNode(String name, Boolean isLeaf, Long firstAppearTime) {
    this.name = name;
    this.isLeaf = isLeaf;
    this.firstAppearTime = firstAppearTime;
  }
}
