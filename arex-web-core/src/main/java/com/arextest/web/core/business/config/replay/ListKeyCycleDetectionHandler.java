package com.arextest.web.core.business.config.replay;

import com.arextest.common.exceptions.ArexException;
import com.arextest.web.common.LogUtils;
import com.arextest.web.common.exception.ArexApiResponseCode;
import com.arextest.web.model.contract.contracts.config.replay.AbstractComparisonDetailsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonListSortConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonReferenceConfiguration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component
public class ListKeyCycleDetectionHandler {

  public <T extends AbstractComparisonDetailsConfiguration> void judgeWhetherCycle(
      ComparisonReferenceConfigurableHandler referenceHandler,
      ComparisonListSortConfigurableHandler listSortHandler,
      T configuration) {

    String appId = configuration.getAppId();
    String operationId = configuration.getOperationId();
    String fsInterfaceId = configuration.getFsInterfaceId();
    String dependencyId = configuration.getDependencyId();

    if (dependencyId != null) {
      // TODO: 2023/9/25 if listsort and reference allow expire, here need to add expire condition
      List<ComparisonReferenceConfiguration> dependencyReferenceInDb =
          referenceHandler.useResultAsList(appId, operationId).stream()
              .filter(reference -> Objects.equals(reference.getDependencyId(), dependencyId))
              .collect(Collectors.toList());

      List<ComparisonListSortConfiguration> dependencyListSortInDb =
          listSortHandler.useResultAsList(appId, operationId).stream()
              .filter(sort -> Objects.equals(sort.getDependencyId(), dependencyId))
              .collect(Collectors.toList());

      this.doCheckCycle(configuration, dependencyReferenceInDb, dependencyListSortInDb);

    } else if (fsInterfaceId != null) {

      List<ComparisonReferenceConfiguration> comparisonReferenceConfigurations =
          referenceHandler.queryByInterfaceId(fsInterfaceId);

      List<ComparisonListSortConfiguration> comparisonListSortConfigurations =
          listSortHandler.queryByInterfaceId(fsInterfaceId);

      this.doCheckCycle(configuration, comparisonReferenceConfigurations,
          comparisonListSortConfigurations);

    } else if (operationId != null) {
      List<ComparisonReferenceConfiguration> dependencyReferenceInDb =
          referenceHandler.useResultAsList(appId, operationId).stream()
              .filter(reference -> StringUtils.isEmpty(reference.getDependencyId()))
              .collect(Collectors.toList());

      List<ComparisonListSortConfiguration> dependencyListSortInDb =
          listSortHandler.useResultAsList(appId, operationId).stream()
              .filter(sort -> StringUtils.isEmpty(sort.getDependencyId()))
              .collect(Collectors.toList());

      this.doCheckCycle(configuration, dependencyReferenceInDb, dependencyListSortInDb);

    } else {
      throw new RuntimeException(
          "reference configuration must have dependencyId or fsInterfaceId or operationId");
    }
  }

  private <T extends AbstractComparisonDetailsConfiguration> void doCheckCycle(T configuration,
      List<ComparisonReferenceConfiguration> referenceInDb,
      List<ComparisonListSortConfiguration> listSortInDb) {

    if (CollectionUtils.isEmpty(referenceInDb) && CollectionUtils.isEmpty(listSortInDb)) {
      return;
    }

    String id = configuration.getId();
    if (configuration instanceof ComparisonReferenceConfiguration) {
      ComparisonReferenceConfiguration newConfiguration = (ComparisonReferenceConfiguration) configuration;
      // update or insert
      if (StringUtils.isNotEmpty(id)) {
        for (int i = 0; i < referenceInDb.size(); i++) {
          ComparisonReferenceConfiguration oldConfiguration = referenceInDb.get(i);
          if (Objects.equals(oldConfiguration.getId(), id)) {
            referenceInDb.set(i, newConfiguration);
          }
        }
      } else {
        referenceInDb.add(newConfiguration);
      }
    } else {

      ComparisonListSortConfiguration newConfiguration = (ComparisonListSortConfiguration) configuration;
      // update or insert
      if (id != null) {
        for (int i = 0; i < listSortInDb.size(); i++) {
          ComparisonListSortConfiguration oldConfiguration = listSortInDb.get(i);
          if (Objects.equals(oldConfiguration.getId(), id)) {
            listSortInDb.set(i, newConfiguration);
          }
        }
      } else {
        listSortInDb.add(newConfiguration);
      }
    }
    this.doCycleDetection(referenceInDb, listSortInDb);
  }

  private void doCycleDetection(List<ComparisonReferenceConfiguration> referenceInDb,
      List<ComparisonListSortConfiguration> listSortInDb) {

    Map<String, List<String>> listKeysMap = new HashMap<>();
    for (ComparisonListSortConfiguration listSort : listSortInDb) {
      if (CollectionUtils.isEmpty(listSort.getListPath()) || CollectionUtils.isEmpty(
          listSort.getKeys())) {
        LogUtils.warn(LOGGER, "listPath or key is empty");
        continue;
      }
      List<List<String>> keys = listSort.getKeys();
      for (List<String> key : keys) {
        if (CollectionUtils.isEmpty(key)) {
          LogUtils.warn(LOGGER, "key is empty");
          continue;
        }
        listKeysMap.computeIfAbsent(this.listToString(listSort.getListPath()),
                k -> new ArrayList<>())
            .add(this.listToString(this.mergePath(listSort.getListPath(), key)));
      }
    }

    // the collection of fkPaths
    Set<String> fkPaths = new HashSet<>();
    // the collection of pkNodeListPaths
    Set<String> pkListPaths = new HashSet<>();

    // fkPath -> the collection of pkNodeListPaths
    Map<String, Set<String>> relations = new HashMap<>();
    for (ComparisonReferenceConfiguration re : referenceInDb) {
      if (CollectionUtils.isEmpty(re.getPkPath()) || CollectionUtils.isEmpty(re.getFkPath())) {
        LogUtils.warn(LOGGER, "pk or fk is empty");
        continue;
      }
      List<String> pkNodeListPath = re.getPkPath().subList(0, re.getPkPath().size() - 1);
      String fkPath = listToString(re.getFkPath());
      String pkListPath = listToString(pkNodeListPath);
      fkPaths.add(fkPath);
      pkListPaths.add(pkListPath);
      relations.computeIfAbsent(fkPath, k -> new HashSet<>()).add(pkListPath);
    }

    // all traversed node collections
    Set<String> traversedSet = new HashSet<>();
    LinkedList<String> queue = new LinkedList<>();

    this.doPriorityReferences(queue, pkListPaths, new LinkedList<>(), traversedSet, relations,
        fkPaths,
        listKeysMap);

  }

  private void doPriorityReferences(LinkedList<String> queue, Set<String> refLinkNodes,
      LinkedList<String> singleLinkAllNodeSet, Set<String> traversedSet,
      Map<String, Set<String>> relations,
      Set<String> fkPaths, Map<String, List<String>> listKeysMap) {
    if (refLinkNodes == null || refLinkNodes.isEmpty()) {
      return;
    }

    for (String refLinkNode : refLinkNodes) {
      if (singleLinkAllNodeSet.contains(refLinkNode)) {
        throw new ArexException(ArexApiResponseCode.LIST_KEY_CIRCLE_ERROR, String.format(
            "the list and referenceThe added configuration will create a cycle with the existing configuration, the path: %s",
            this.circlePathToShow(singleLinkAllNodeSet)));
      }

      if (traversedSet.contains(refLinkNode)) {
        continue;
      }

      queue.addLast(refLinkNode);
      traversedSet.add(refLinkNode);

      Set<String> refFkNodePaths = findFkPathInListKey(fkPaths, refLinkNode, listKeysMap);

      for (String refFkNode : refFkNodePaths) {
        singleLinkAllNodeSet.addLast(refLinkNode);
        Set<String> refPkListPaths = relations.get(refFkNode);
        this.doPriorityReferences(queue, refPkListPaths, singleLinkAllNodeSet, traversedSet,
            relations, fkPaths,
            listKeysMap);
        singleLinkAllNodeSet.removeLast();
      }
    }
  }

  private String listToString(List<String> listPath) {
    if (CollectionUtils.isEmpty(listPath)) {
      return null;
    }
    return String.join("/", listPath);
  }

  private List<String> mergePath(List<String> listPath, List<String> key) {
    List<String> result = new ArrayList<>(listPath);
    result.addAll(key);
    return result;
  }

  /**
   * find all fkPaths in pkNodeListPath
   *
   * @param fkPaths        the collection of fkPaths
   * @param pkNodeListPath pkNodeListPath
   * @return the collection of fkPaths
   */
  private Set<String> findFkPathInListKey(Set<String> fkPaths, String pkNodeListPath,
      Map<String, List<String>> listKeysMap) {
    List<String> keyPaths = listKeysMap.get(pkNodeListPath);
    if (keyPaths == null || keyPaths.isEmpty()) {
      return Collections.emptySet();
    }

    Set<String> matchPath = new HashSet<>(keyPaths.size());
    for (String keyPath : keyPaths) {
      if (fkPaths.contains(keyPath)) {
        matchPath.add(keyPath);
      }
    }
    return matchPath;
  }

  private String circlePathToShow(List<String> circlePath) {
    if (CollectionUtils.isEmpty(circlePath)) {
      return null;
    }
    String nodePath = String.join(" -> ", circlePath);
    nodePath = nodePath + " -> " + circlePath.get(0);
    return nodePath;
  }

}
