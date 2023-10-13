package com.arextest.web.core.business;

import com.arextest.common.context.ArexContext;
import com.arextest.common.utils.JsonTraverseUtils;
import com.arextest.web.core.business.util.JsonUtils;
import com.arextest.web.core.business.util.ListUtils;
import com.arextest.web.core.repository.mongo.ReplayCompareResultRepositoryImpl;
import com.arextest.web.model.contract.contracts.QueryMsgWithDiffRequestType;
import com.arextest.web.model.contract.contracts.QueryMsgWithDiffResponseType;
import com.arextest.web.model.contract.contracts.common.LogEntity;
import com.arextest.web.model.contract.contracts.common.NodeEntity;
import com.arextest.web.model.contract.contracts.common.UnmatchedPairEntity;
import com.arextest.web.model.contract.contracts.common.UnmatchedType;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.enums.DiffResultCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Component
public class MsgShowService {

    @Resource
    ReplayCompareResultRepositoryImpl replayCompareResultRepository;

    private static final String EMPTY = "%empty%";

    private static final Set<Integer> SHOW_UNMATCHED_TYPES = new HashSet<>(Arrays.asList(
            UnmatchedType.UNMATCHED,
            UnmatchedType.RIGHT_MISSING,
            UnmatchedType.LEFT_MISSING));

    @Resource(name = "message-clip-executor")
    ThreadPoolTaskExecutor executor;

    private static final ObjectMapper COMPARE_OBJECT_MAPPER = new ObjectMapper();

    public QueryMsgWithDiffResponseType queryMsgWithDiff(QueryMsgWithDiffRequestType request) {
        QueryMsgWithDiffResponseType response = new QueryMsgWithDiffResponseType();
        CompareResultDto compareResultDto = replayCompareResultRepository.queryCompareResultsById(request.getCompareResultId());
        if (compareResultDto == null) {
            return response;
        }

        if (!Boolean.TRUE.equals(ArexContext.getContext().getPassAuth())) {
            JsonUtils.downgrade(compareResultDto);
        }


        String baseMsg = compareResultDto.getBaseMsg();
        String testMsg = compareResultDto.getTestMsg();

        response.setReplayId(compareResultDto.getReplayId());
        response.setRecordId(compareResultDto.getRecordId());
        response.setDiffResultCode(compareResultDto.getDiffResultCode());

        List<LogEntity> sceneLogs = null;
        if (compareResultDto.getDiffResultCode() == DiffResultCode.COMPARED_INTERNAL_EXCEPTION) {
            sceneLogs = compareResultDto.getLogs();
        } else {
            List<Integer> logIndexes = Arrays.stream(request.getLogIndexes().split("_")).map(Integer::parseInt).collect(Collectors.toList());
            // Get logs in a single scenario
            sceneLogs = getSceneLogs(logIndexes, compareResultDto.getLogs());
            // Construct a new message based on the original message and the logs in the scenario, and switch the error path in the logs to the path of the new message
            if (baseMsg != null && testMsg != null) {
                MutablePair<Object, Object> baseAndTestObjCombination = produceNewObjectFromOriginal(baseMsg, testMsg, sceneLogs);
                baseMsg = baseAndTestObjCombination.getLeft().toString();
                testMsg = baseAndTestObjCombination.getRight().toString();
            }
        }

        response.setBaseMsg(baseMsg);
        response.setTestMsg(testMsg);
        response.setLogs(sceneLogs);
        return response;
    }

    private List<LogEntity> getSceneLogs(List<Integer> logIndexes, List<LogEntity> logs) {
        List<LogEntity> sceneLogs = new ArrayList<>();
        for (Integer logIndex : logIndexes) {
            if (SHOW_UNMATCHED_TYPES.contains(logs.get(logIndex).getPathPair().getUnmatchedType())) {
                sceneLogs.add(logs.get(logIndex));
            }
        }

        Collections.sort(sceneLogs, new Comparator<LogEntity>() {
            @Override
            public int compare(LogEntity o1, LogEntity o2) {

                List<NodeEntity> leftUnmatchedPath1 = o1.getPathPair().getLeftUnmatchedPath();
                List<NodeEntity> rightUnmatchedPath1 = o1.getPathPair().getRightUnmatchedPath();
                List<NodeEntity> leftUnmatchedPath2 = o2.getPathPair().getLeftUnmatchedPath();
                List<NodeEntity> rightUnmatchedPath2 = o2.getPathPair().getRightUnmatchedPath();
                if (leftUnmatchedPath1.size() > leftUnmatchedPath2.size()) {
                    return -1;
                } else if (leftUnmatchedPath1.size() < leftUnmatchedPath2.size()) {
                    return 1;
                } else {
                    String leftPath1 = ListUtils.convertPathToStringForShow(leftUnmatchedPath1);
                    String rightPath1 = ListUtils.convertPathToStringForShow(rightUnmatchedPath1);
                    String leftPath2 = ListUtils.convertPathToStringForShow(leftUnmatchedPath2);
                    String rightPath2 = ListUtils.convertPathToStringForShow(rightUnmatchedPath2);
                    if (!leftPath1.equals(leftPath2)) {
                        return leftPath1.compareTo(leftPath2);
                    } else {
                        return rightPath1.compareTo(rightPath2);
                    }
                }
            }
        });
        return sceneLogs;
    }

    public MutablePair<Object, Object> produceNewObjectFromOriginal(String baseMsg, String testMsg, List<LogEntity> sceneLogs) {

        List<CompletableFuture<Object>> parseTaskList =
                Stream.of(baseMsg, testMsg, baseMsg, testMsg)
                        .map(item -> CompletableFuture.supplyAsync(() -> {
                            try {
                                return objectParse(item);
                            } catch (JsonProcessingException e) {
                                return null;
                            }
                        }, executor))
                        .collect(Collectors.toList());
        CompletableFuture<List<Object>> listCompletableFuture =
                CompletableFuture.allOf(parseTaskList.toArray(new CompletableFuture[0]))
                        .thenApply(v -> parseTaskList.stream().map(parseTask -> {
                            try {
                                return parseTask.get();
                            } catch (Exception e) {
                                return null;
                            }
                        }).collect(Collectors.toList()));
        List<Object> objectList = listCompletableFuture.join();
        Object baseObj = objectList.get(0);
        Object testObj = objectList.get(1);
        Object constructedBaseObj = objectList.get(2);
        Object constructedTestObj = objectList.get(3);

        if (baseObj == null || testObj == null) {
            return new MutablePair<>(baseMsg, testMsg);
        }


        CompletableFuture.allOf(
                Stream.of(constructedBaseObj, constructedTestObj)
                        .map(item -> CompletableFuture.runAsync(
                                () -> cropJSONArray(item), executor)).toArray(CompletableFuture[]::new))
                .join();


        ArrayOrder baseArrayOrder = new ArrayOrder();
        ArrayOrder testArrayOrder = new ArrayOrder();

        for (LogEntity logEntity : sceneLogs) {
            UnmatchedPairEntity pathPair = logEntity.getPathPair();
            int unmatchedType = pathPair.getUnmatchedType();


            if (unmatchedType == UnmatchedType.DIFFERENT_COUNT) {
                continue;
            }

            List<NodeEntity> leftUnmatchedPath = pathPair.getLeftUnmatchedPath();
            List<NodeEntity> rightUnmatchedPath = pathPair.getRightUnmatchedPath();


            processPath(leftUnmatchedPath, rightUnmatchedPath, unmatchedType);


            List<NodeEntity> constructedLeftUnmatchedPath = fillConstructedObjFromOriginal(pathPair.getLeftUnmatchedPath(), baseObj, constructedBaseObj, baseArrayOrder);
            List<NodeEntity> constructedRightUnmatchedPath = fillConstructedObjFromOriginal(pathPair.getRightUnmatchedPath(), testObj, constructedTestObj, testArrayOrder);


            pathPair.setLeftUnmatchedPath(constructedLeftUnmatchedPath);
            pathPair.setRightUnmatchedPath(constructedRightUnmatchedPath);
        }

        return new MutablePair<>(constructedBaseObj, constructedTestObj);
    }

    private Object objectParse(String msg) throws JsonProcessingException {
        Object obj = null;
        if (msg.startsWith("[")) {
            obj = COMPARE_OBJECT_MAPPER.readValue(msg, ArrayNode.class);
        } else {
            obj = COMPARE_OBJECT_MAPPER.readValue(msg, ObjectNode.class);
        }
        return obj;
    }

    private Integer findFirstArrayNode(List<NodeEntity> path) {
        int result = -1;
        for (int i = 0; i < path.size(); i++) {
            NodeEntity nodeEntity = path.get(i);
            if (nodeEntity.getNodeName() == null) {
                return i;
            }
        }
        return result;
    }

    private void processPath(List<NodeEntity> leftUnmatchedPath, List<NodeEntity> rightUnmatchedPath, Integer unmatchedType) {
        if (unmatchedType == UnmatchedType.LEFT_MISSING) {
            if (leftUnmatchedPath.size() < rightUnmatchedPath.size()) {
                int size = leftUnmatchedPath.size();
                NodeEntity nodeEntity = rightUnmatchedPath.get(size);
                if (nodeEntity.getNodeName() != null) {
                    leftUnmatchedPath.add(new NodeEntity(EMPTY, 0));
                } else {
                    leftUnmatchedPath.add(new NodeEntity(null, Integer.MIN_VALUE));
                }
            }
        } else if (unmatchedType == UnmatchedType.RIGHT_MISSING) {
            if (leftUnmatchedPath.size() > rightUnmatchedPath.size()) {
                int size = rightUnmatchedPath.size();
                NodeEntity nodeEntity = leftUnmatchedPath.get(size);
                if (nodeEntity.getNodeName() != null) {
                    rightUnmatchedPath.add(new NodeEntity(EMPTY, 0));
                } else {
                    rightUnmatchedPath.add(new NodeEntity(null, Integer.MIN_VALUE));
                }
            }
        }

    }

    private List<NodeEntity> processAddedPath(List<NodeEntity> unmatchedPath) {
        if (unmatchedPath == null || unmatchedPath.isEmpty()) {
            return unmatchedPath;
        }
        NodeEntity nodeEntity = unmatchedPath.get(unmatchedPath.size() - 1);
        if (Objects.equals(nodeEntity.getNodeName(), EMPTY) || Objects.equals(nodeEntity.getIndex(), Integer.MIN_VALUE)) {
            unmatchedPath.remove(unmatchedPath.size() - 1);
        }
        return unmatchedPath;
    }

    private List<NodeEntity> fillConstructedObjFromOriginal(List<NodeEntity> unmatchedPath, Object obj, Object constructedObj, ArrayOrder arrayOrder) {

        List<NodeEntity> constructedUnmatchedPath = new ArrayList<>();

        for (int i = 0; i < unmatchedPath.size(); i++) {
            JsonNode tempObj = null;
            JsonNode tempConstructedObj = null;
            ArrayOrder tempArrayOrder = new ArrayOrder();
            NodeEntity nodePath = unmatchedPath.get(i);

            if (obj instanceof ObjectNode) {
                ObjectNode obj1 = (ObjectNode) obj;
                ObjectNode constructedObj1 = (ObjectNode) constructedObj;

                String nodeName = nodePath.getNodeName();
                if (Objects.equals(nodeName, EMPTY)) {
                    return constructedUnmatchedPath;
                }
                tempObj = obj1.get(nodeName);

                tempConstructedObj = constructedObj1.get(nodeName);
                if (tempConstructedObj == null) {
                    if (tempObj instanceof ObjectNode) {
                        tempConstructedObj = COMPARE_OBJECT_MAPPER.createObjectNode();
                    } else if (tempObj instanceof ArrayNode) {
                        tempConstructedObj = COMPARE_OBJECT_MAPPER.createArrayNode();
                    } else {
                        tempConstructedObj = tempObj;
                    }
                    constructedObj1.set(nodeName, tempConstructedObj);
                }

                if (arrayOrder.getObjectStructure().containsKey(nodeName)) {
                    tempArrayOrder = arrayOrder.getObjectStructure().get(nodeName);
                } else {
                    arrayOrder.getObjectStructure().put(nodeName, tempArrayOrder);
                }
                constructedUnmatchedPath.add(new NodeEntity(nodeName, 0));
            } else if (obj instanceof ArrayNode) {
                ArrayNode obj1 = (ArrayNode) obj;
                ArrayNode constructedObj1 = (ArrayNode) constructedObj;

                int beforeIndex = nodePath.getIndex();
                int curIndex = constructedObj1.size();
                if (beforeIndex == Integer.MIN_VALUE) {
                    return constructedUnmatchedPath;
                }
                tempObj = obj1.get(beforeIndex);

                if (arrayOrder.getOrder().containsKey(beforeIndex)) {
                    Integer existArrayIndex = arrayOrder.getOrder().get(beforeIndex);
                    constructedUnmatchedPath.add(new NodeEntity(null, existArrayIndex));

                } else {

                    arrayOrder.getOrder().put(beforeIndex, curIndex);
                    arrayOrder.getObjectStructure().put(curIndex, tempArrayOrder);

                    Integer arrayIndex = findFirstArrayNode(unmatchedPath);
                    if (i == arrayIndex) {
                        constructedObj1.insert(curIndex, tempObj);
                        constructedUnmatchedPath.add(new NodeEntity(null, curIndex));
                    }
                }
                constructedUnmatchedPath.addAll(processAddedPath(unmatchedPath.subList(i + 1, unmatchedPath.size())));
                return constructedUnmatchedPath;
            }

            obj = tempObj;
            constructedObj = tempConstructedObj;
            arrayOrder = tempArrayOrder;
        }
        return constructedUnmatchedPath;
    }

    private void cropJSONArray(Object obj) {
        if (obj instanceof ObjectNode) {
            ObjectNode obj1 = (ObjectNode) obj;
            Iterator<String> stringIterator = obj1.fieldNames();
            List<String> names = new ArrayList<>();
            while (stringIterator.hasNext()) {
                names.add(stringIterator.next());
            }
            for (String name : names) {
                JsonNode tempObj = obj1.get(name);
                if (tempObj instanceof ArrayNode) {
                    obj1.remove(name);
                } else {
                    cropJSONArray(tempObj);
                }
            }
        } else if (obj instanceof ArrayNode) {
            ArrayNode objArr = ((ArrayNode) obj);
            int length = objArr.size();
            for (int i = length - 1; i >= 0; i--) {
                objArr.remove(i);
            }
        }
    }

    @Data
    @NoArgsConstructor
    private class ArrayOrder {

        private Map<Integer, Integer> order = new HashMap<>();

        private Map<Object, ArrayOrder> objectStructure = new HashMap<>();
    }
}
