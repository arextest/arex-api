package com.arextest.web.core.business;

import com.arextest.diff.utils.ListUti;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.util.ListUtils;
import com.arextest.web.core.repository.ReportDiffAggStatisticRepository;
import com.arextest.web.model.contract.contracts.common.LogEntity;
import com.arextest.web.model.contract.contracts.common.LogTag;
import com.arextest.web.model.contract.contracts.common.NodeEntity;
import com.arextest.web.model.contract.contracts.common.UnmatchedPairEntity;
import com.arextest.web.model.contract.contracts.common.UnmatchedType;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.dto.DiffAggDto;
import com.arextest.web.model.dto.SceneDetailDto;
import com.arextest.web.model.enums.DiffResultCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class SceneService {

    private static final String LEFT_MISSING_SUFFIX = "@L";
    private static final String BASE_MISSING = "%baseMissing%";
    private static final String TEST_MISSING = "%testMissing%";

    @Resource
    private ReportDiffAggStatisticRepository reportDiffAggStatisticRepository;


    /**
     * Keys of diffAgg
     */
    @Data
    private class DiffAggKey {
        public DiffAggKey(String planItemId, String categoryName, String operationName) {
            this.planItemId = planItemId;
            this.categoryName = categoryName;
            this.operationName = operationName;
        }
        private String planItemId;
        private String categoryName;
        private String operationName;
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            DiffAggKey that = (DiffAggKey) o;
            return Objects.equals(planItemId, that.planItemId) && Objects.equals(categoryName,
                    that.categoryName) && Objects.equals(operationName, that.operationName);
        }
        @Override
        public int hashCode() {
            return Objects.hash(planItemId, categoryName, operationName);
        }
    }


    private static Map<DiffAggKey, DiffAggDto> result = new HashMap<>();

    /**
     * calculate multi compareResults
     */
    public void statisticScenes(List<CompareResultDto> dtos) {
        if (dtos == null) {
            return;
        }
        dtos.forEach(dto -> {
            statisticScenes(dto);
        });
    }

    /**
     * statistic differents and scenes
     */
    public void statisticScenes(CompareResultDto compareResultDto) {
        if (compareResultDto == null) {
            return;
        }
        if (compareResultDto.getDiffResultCode() == DiffResultCode.COMPARED_WITHOUT_DIFFERENCE) {
            return;
        }
        Map<String, Map<String, List<Pair<Integer, String>>>> caseMap = new HashMap<>();
        if (compareResultDto.getDiffResultCode() == DiffResultCode.COMPARED_WITH_DIFFERENCE) {
            if (compareResultDto.getLogs() == null || compareResultDto.getLogs().size() == 0) {
                return;
            }
            /** caseMap Stores the overall data of the error scene according to the precise path.
             * (Not deduplicated according to the scene)
             * key:path(without index)
             * value:
             *      key:path(with index)
             *      value: Pair<integer,string>
             *          integer: logentity index,
             *          string: last node name of path
             */

            if (isBaseOrTestMissing(compareResultDto)) {
                LogEntity log = compareResultDto.getLogs().get(0);
                if (log.getBaseValue() == null && log.getTestValue() != null) {
                    setBaseTestMissingScene(caseMap, BASE_MISSING);
                }
                if (log.getBaseValue() != null && log.getTestValue() == null) {
                    setBaseTestMissingScene(caseMap, TEST_MISSING);
                }
            } else {
                for (int i = 0; i < compareResultDto.getLogs().size(); i++) {
                    LogEntity log = compareResultDto.getLogs().get(i);
                    Pair<List<NodeEntity>, String> pathNodeNamePair = getPath(log.getPathPair());
                    if (pathNodeNamePair == null) {
                        continue;
                    }
                    String pathStr = getPathStr(pathNodeNamePair.getLeft());
                    String fuzzyPathStr = ListUtils.getFuzzyPathStr(pathNodeNamePair.getLeft());
                    if (!caseMap.containsKey(fuzzyPathStr)) {
                        caseMap.put(fuzzyPathStr, new HashMap<>());
                    }
                    Map<String, List<Pair<Integer, String>>> scene = caseMap.get(fuzzyPathStr);
                    if (!scene.containsKey(pathStr)) {
                        scene.put(pathStr, new ArrayList<>());
                    }
                    List<Pair<Integer, String>> logIndexes = scene.get(pathStr);

                    logIndexes.add(new MutablePair<>(i, pathNodeNamePair.getRight()));
                }
            }
        } else if (compareResultDto.getDiffResultCode() == DiffResultCode.COMPARED_INTERNAL_EXCEPTION) {
            /**
             * Invalid cases are temporarily not included in the difference statistics
             *
             Pair<Integer, String> pair = new MutablePair<>(-1, "invalidCase");
             List<Pair<Integer, String>> list = new ArrayList<>();
             list.add(pair);
             Map<String, List<Pair<Integer, String>>> map = new HashMap<>();
             map.put("", list);
             caseMap.put("", map);
             */
        }

        synchronized (result) {
            DiffAggKey key = new DiffAggKey(compareResultDto.getPlanItemId(),
                    compareResultDto.getCategoryName(),
                    compareResultDto.getOperationName());

            // computing scene
            if (!result.containsKey(key)) {
                DiffAggDto diffAggDto = new DiffAggDto();
                diffAggDto.setDifferences(new HashMap<>());
                diffAggDto.setPlanId(compareResultDto.getPlanId());
                diffAggDto.setPlanItemId(compareResultDto.getPlanItemId());
                diffAggDto.setOperationId(compareResultDto.getOperationId());
                diffAggDto.setCategoryName(compareResultDto.getCategoryName());
                diffAggDto.setOperationName(compareResultDto.getOperationName());
                diffAggDto.setDiffCaseCounts(new HashMap<>());
                result.put(key, diffAggDto);
            }
            DiffAggDto diffAggDto = result.get(key);

            for (Map.Entry<String, Map<String, List<Pair<Integer, String>>>> caseEntry : caseMap.entrySet()) {

                // 2022/01/12 Add and calculate the count of case with difference points
                Map<String, Integer> diffCaseCountMap = diffAggDto.getDiffCaseCounts();
                if (!diffCaseCountMap.containsKey(caseEntry.getKey())) {
                    diffCaseCountMap.put(caseEntry.getKey(), 1);
                } else {
                    diffCaseCountMap.put(caseEntry.getKey(), diffCaseCountMap.get(caseEntry.getKey()) + 1);
                }

                Map<String, Map<String, SceneDetailDto>> fuzzyPathMapResult = diffAggDto.getDifferences();

                if (!fuzzyPathMapResult.containsKey(caseEntry.getKey())) {
                    fuzzyPathMapResult.put(caseEntry.getKey(), new HashMap<>());
                }
                Map<String, SceneDetailDto> sceneResult = fuzzyPathMapResult.get(caseEntry.getKey());

                for (Map.Entry<String, List<Pair<Integer, String>>> caseItemEntry : caseEntry.getValue().entrySet()) {
                    String sceneKey = getSceneKey(caseItemEntry.getValue());
                    if (!sceneResult.containsKey(sceneKey)) {
                        SceneDetailDto detailDto = new SceneDetailDto();
                        detailDto.setLogIndexes(getLogEntityIndexes(caseItemEntry.getValue()));
                        detailDto.setCompareResultId(compareResultDto.getId());
                        detailDto.setSceneCount(0);
                        sceneResult.put(sceneKey, detailDto);
                    }
                    SceneDetailDto detailDto = sceneResult.get(sceneKey);
                    detailDto.setSceneCount(detailDto.getSceneCount() + 1);
                }
            }
        }
    }

    private boolean isBaseOrTestMissing(CompareResultDto dto) {
        if (dto.getLogs().size() > 1) {
            return false;
        }
        LogEntity log = dto.getLogs().get(0);
        if (CollectionUtils.isNotEmpty(log.getPathPair().getLeftUnmatchedPath())
                || CollectionUtils.isNotEmpty(log.getPathPair().getRightUnmatchedPath())) {
            return false;
        }
        if (log.getBaseValue() != null && log.getTestValue() != null) {
            return false;
        }
        return true;
    }

    private void setBaseTestMissingScene(Map<String, Map<String, List<Pair<Integer, String>>>> caseMap,
            String baseMissing) {
        caseMap.put(baseMissing, new HashMap<>());
        Map<String, List<Pair<Integer, String>>> sceneMap = caseMap.get(baseMissing);
        sceneMap.put(baseMissing, new ArrayList<>());
        List<Pair<Integer, String>> scene = sceneMap.get(baseMissing);
        scene.add(new MutablePair<>(0, baseMissing));
    }

    /**
     * Enter the calculated scene information into the database by means of a job
     */
    public void report() {
        if (result == null) {
            return;
        }
        synchronized (result) {
            if (result.size() == 0) {
                return;
            }

            StopWatch sw = new StopWatch();
            sw.start("scene items");

            for (Map.Entry<DiffAggKey, DiffAggDto> diffScene : result.entrySet()) {
                reportDiffAggStatisticRepository.updateDiffScenes(diffScene.getValue());
            }
            result.clear();

            sw.stop();
            LogUtils.info(LOGGER, sw.toString());
        }
    }

    private String getLogEntityIndexes(List<Pair<Integer, String>> scenePart) {
        if (scenePart == null || scenePart.size() == 0) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (Pair<Integer, String> s : scenePart) {
            if (sb.length() != 0) {
                sb.append("_");
            }
            sb.append(s.getLeft());
        }
        return sb.toString();
    }

    /**
     * Return the key (node combination) of the scene according to the node name,
     * without sorting (if the future scene is repeated due to the order, it is recommended to do sorting)
     */
    private String getSceneKey(List<Pair<Integer, String>> scenePart) {
        if (scenePart == null || scenePart.size() == 0) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (Pair<Integer, String> s : scenePart) {
            if (sb.length() != 0) {
                sb.append("_");
            }
            sb.append(s.getRight());
        }
        return sb.toString();
    }



    /**
     * calculate path with index for array
     */
    private String getPathStr(List<NodeEntity> path) {
        if (path == null || path.size() == 0) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder(path.size() * 10);
        for (NodeEntity p : path) {
            if (!StringUtils.isEmpty(p.getNodeName())) {
                if (sb.length() != 0) {
                    sb.append("\\");
                }
                sb.append(p.getNodeName());
            } else {
                sb.append("[").append(p.getIndex()).append("]");
            }
        }
        return sb.toString();
    }

    /**
     * @return : MutablePair<List<NodeEntity>,String>
     * left: List<NodeEntity> List of nodes to aggregate，for calculating fuzzyPath and path
     * right: string last name of nodeEntity, for calculating scenes
     */
    private MutablePair<List<NodeEntity>, String> getPath(UnmatchedPairEntity entity) {

        if (entity == null) {
            return null;
        }
        if (entity.getLeftUnmatchedPath() == null || entity.getLeftUnmatchedPath().size() == 0) {
            return new MutablePair<>(new ArrayList<>(), StringUtils.EMPTY);
        }
        if (entity.getLeftUnmatchedPath().size() == 1) {
            return new MutablePair<>(entity.getLeftUnmatchedPath(), StringUtils.EMPTY);
        }
        if (entity.getUnmatchedType() == UnmatchedType.LEFT_MISSING) {
            String nodeName = Strings.EMPTY;
            if (!StringUtils.isEmpty(entity.getRightUnmatchedPath()
                    .get(entity.getRightUnmatchedPath().size() - 1)
                    .getNodeName())) {
                nodeName = entity.getRightUnmatchedPath().get(entity.getRightUnmatchedPath().size() - 1).getNodeName();
            }
            return new MutablePair<>(entity.getLeftUnmatchedPath(), nodeName + LEFT_MISSING_SUFFIX);
        } else {
            String nodeName = Strings.EMPTY;
            if (!StringUtils.isEmpty(entity.getLeftUnmatchedPath()
                    .get(entity.getLeftUnmatchedPath().size() - 1)
                    .getNodeName())) {
                nodeName = entity.getLeftUnmatchedPath().get(entity.getLeftUnmatchedPath().size() - 1).getNodeName();
            }
            return new MutablePair<>(entity.getLeftUnmatchedPath().subList(0, entity.getLeftUnmatchedPath().size() - 1),
                    nodeName);
        }
    }

    public static void main(String[] args) {
        CompareResultDto dto = new CompareResultDto();
        List<LogEntity> logs = new ArrayList<>();
        LogEntity log = new LogEntity();

        UnmatchedPairEntity entity = new UnmatchedPairEntity();
        List<NodeEntity> left = new ArrayList<>();
        // left.add(new NodeEntity(null, 6));
        // left.add(new NodeEntity("test", 0));
        // left.add(new NodeEntity(null, 3));
        // left.add(new NodeEntity("Subject", 0));
        entity.setLeftUnmatchedPath(left);
        entity.setUnmatchedType(UnmatchedType.UNMATCHED);
        log.setBaseValue("aaa");
        log.setPathPair(entity);
        log.setLogTag(new LogTag());
        logs.add(log);
        dto.setLogs(logs);
        dto.setDiffResultCode(DiffResultCode.COMPARED_WITH_DIFFERENCE);

        new SceneService().statisticScenes(dto);

    }
}
