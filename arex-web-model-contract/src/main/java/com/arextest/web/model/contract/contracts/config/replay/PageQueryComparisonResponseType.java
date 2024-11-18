package com.arextest.web.model.contract.contracts.config.replay;

import com.arextest.web.model.contract.PagingResponse;
import com.arextest.web.model.contract.contracts.compare.CategoryDetail;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonScriptConfiguration.ScriptMethod;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;

@Data
public class PageQueryComparisonResponseType implements PagingResponse {

  private Long totalCount;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  List<ExclusionInfo> exclusions;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  List<InclusionInfo> inclusions;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  List<IgnoreCategoryInfo> ignoreCategories;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  List<ListSortInfo> listSorts;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  List<ReferenceInfo> referenceInfos;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  List<RootTransformInfo> rootTransformInfos;

  List<ScriptInfo> scriptInfos;


  @Data
  public static class ExclusionInfo extends BaseConfigInfo {

    private List<String> exclusionPath;
  }

  @Data
  public static class InclusionInfo extends BaseConfigInfo {

    private List<String> inclusionPath;
  }

  @Data
  public static class IgnoreCategoryInfo extends BaseConfigInfo {

    private CategoryDetail ignoreCategoryDetail;
  }

  @Data
  public static class ListSortInfo extends BaseConfigInfo {

    private List<String> listPath;
    private List<List<String>> keys;
  }

  @Data
  public static class ReferenceInfo extends BaseConfigInfo {

    private List<String> pkPath;
    private List<String> fkPath;
  }

  @Data
  public static class RootTransformInfo extends BaseConfigInfo {

    private String transformMethodName;
  }

  @Data
  public static class ScriptInfo extends BaseConfigInfo {

    private List<String> nodePath;

    private ScriptMethod scriptMethod;
  }


  @Data
  public static class BaseConfigInfo {

    private String id;
    private String operationName;
    private String dependencyName;
    private String dependencyType;
    private Long expirationDate;
    private Integer expirationType;
  }


}
