package com.arextest.web.model.dto.vertexai;

import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @author: QizhengMo
 * @date: 2024/3/25 13:23
 */
@Data
public class VertexRes {
  private List<Prediction> predictions;

  @Data
  public static class Prediction {
    private List<Candidate> candidates;
    private List<CitationMeta> citationMetadata;
    private List<SafetyAttribute> safetyAttributes;
  }

  @Data
  public static class Candidate {
    private String author;
    private String content;
  }

  @Data
  public static class CitationMeta {
    private List<Citation> citations;
  }

  @Data
  public static class Citation {
    private Integer startIndex;
    private Integer endIndex;
    private String url;
    private String title;
    private String license;
    private String publicationDate;
  }

  @Data
  public static class Logprobs {
    private List<Float> tokenLogProbs;
    private List<String> tokens;
    private List<Map<String, Float>> topLogProbs;
  }

  @Data
  public static class SafetyAttribute {
    private List<String> categories;
    private Boolean blocked;
    private List<Float> scores;
  }
}
