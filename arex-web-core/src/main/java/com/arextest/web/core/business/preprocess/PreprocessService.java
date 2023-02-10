package com.arextest.web.core.business.preprocess;

import com.arextest.common.utils.CompressionUtils;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.MessagePreprocessRepository;
import com.arextest.web.core.repository.PreprocessConfigRepository;
import com.arextest.web.core.repository.ServletMockerRepository;
import com.arextest.web.model.dto.MessagePreprocessDto;
import com.arextest.web.model.dto.PreprocessConfigDto;
import com.arextest.web.model.dto.ServletMockerDto;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class PreprocessService {
    private static final String SERVLET_MOCKER = "servletMocker";
    private static final Integer STEP = 100;

    @Resource
    private MessagePreprocessRepository messagePreprocessRepository;

    @Resource
    private ServletMockerRepository servletMockerRepository;

    @Resource
    private PreprocessConfigRepository preprocessConfigRepository;

    @Resource(name = "schemaCache")
    private LoadingCache schemaCache;

    public void updateServletSchema() {
        LogUtils.info(LOGGER, "starting update servlet schema");
        PreprocessConfigDto preprocessConfigDto = preprocessConfigRepository.queryConfig(SERVLET_MOCKER);
        String index = StringUtils.EMPTY;
        if (preprocessConfigDto != null && StringUtils.isNotEmpty(preprocessConfigDto.getIndex())) {
            index = preprocessConfigDto.getIndex();
        }
        boolean stop = false;
        do {
            List<ServletMockerDto> dtos = servletMockerRepository.queryServletMockers(index, STEP);
            if (dtos == null || dtos.size() == 0) {
                break;
            }
            if (dtos.size() < STEP) {
                stop = true;
            }
            index = dtos.get(dtos.size() - 1).getId();
            preprocessConfigRepository.updateIndex(SERVLET_MOCKER, index);
            for (ServletMockerDto dto : dtos) {
                if (StringUtils.isNotEmpty(dto.getRequest())) {
                    String key = dto.getAppId() + "_" + dto.getPath() + "_request";
                    try {
                        String request = CompressionUtils.useZstdDecompress(dto.getRequest());
                        String requestJson = new String(Base64.getDecoder().decode(request));
                        updateSchema(key, requestJson);
                    } catch (Exception e) {
                        LogUtils.error(LOGGER,
                                String.format("failed to preprocess request. Key:%s, Id:%s", key, dto.getId()), e);
                    }
                }
                if (StringUtils.isNotEmpty(dto.getResponse())) {
                    String key = dto.getAppId() + "_" + dto.getPath() + "_response";
                    try {
                        String response = CompressionUtils.useZstdDecompress(dto.getResponse());
                        updateSchema(key, response);
                    } catch (Exception e) {
                        LogUtils.error(LOGGER,
                                String.format("failed to preprocess response. Key:%s, Id:%s", key, dto.getId()), e);
                    }
                }
            }
            LogUtils.info(LOGGER, String.format("preprocess %d records", dtos.size()));
        } while (!stop);
        LogUtils.info(LOGGER, "finish updating servlet schema");
    }

    public Boolean updateSchema(String key, String message) throws JSONException {
        Object json = parseJson(message);
        if (json == null) {
            return false;
        }

        PreprocessTreeNode root = (PreprocessTreeNode) schemaCache.get(key);
        if (root == null) {
            root = new PreprocessTreeNode();
        }

        Set<String> newPaths = new HashSet<>();
        List<String> currentPath = new ArrayList<>();

        if (json instanceof JSONObject) {
            JsonObjectIter((JSONObject) json, currentPath, newPaths, root);
        } else {
            JsonArrayIter((JSONArray) json, currentPath, newPaths, root);
        }

        saveNewNodes(key, newPaths);
        schemaCache.refresh(key);
        return true;
    }

    private void JsonObjectIter(JSONObject obj,
            List<String> currentPath,
            Set<String> newPaths,
            PreprocessTreeNode root) throws JSONException {
        if (obj == null) {
            return;
        }

        Iterator<String> keys = obj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = obj.get(key);
            currentPath.add(key);

            if (value instanceof JSONObject) {
                JsonObjectIter((JSONObject) value, currentPath, newPaths, root);
            } else if (value instanceof JSONArray) {
                JsonArrayIter((JSONArray) value, currentPath, newPaths, root);
            } else {
                String path = getPathString(currentPath);
                if (!newPaths.contains(path) && !isExistInTree(root, currentPath)) {
                    newPaths.add(path);
                }
            }
            currentPath.remove(currentPath.size() - 1);
        }
    }

    private void JsonArrayIter(JSONArray array,
            List<String> currentPath,
            Set<String> newPaths,
            PreprocessTreeNode root) throws JSONException {
        if (array == null || array.length() == 0) {
            return;
        }
        for (int i = 0; i < array.length(); i++) {
            Object item = array.get(i);
            if (item instanceof JSONObject) {
                JsonObjectIter((JSONObject) item, currentPath, newPaths, root);
            } else if (item instanceof JSONArray) {
                JsonArrayIter((JSONArray) item, currentPath, newPaths, root);
            } else {
                currentPath.add("%value%");
                String path = getPathString(currentPath);
                if (!isExistInTree(root, currentPath)) {
                    newPaths.add(path);
                }
                currentPath.remove(currentPath.size() - 1);
            }
        }
    }

    private Object parseJson(String message) {
        if (message.startsWith("{")) {
            try {
                return new JSONObject(message);
            } catch (JSONException e) {
                return null;
            }
        }
        if (message.startsWith("[")) {
            try {
                return new JSONArray(message);
            } catch (JSONException e) {
                return null;
            }
        }
        return null;
    }

    private String getPathString(List<String> pathNodes) {
        if (pathNodes == null || pathNodes.size() == 0) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (String path : pathNodes) {
            if (sb.length() != 0) {
                sb.append("/");
            }
            sb.append(path);
        }
        return sb.toString();
    }

    private Boolean isExistInTree(PreprocessTreeNode root, List<String> pathNodes) {
        if (pathNodes == null || pathNodes.size() == 0) {
            return false;
        }
        if (root == null) {
            return false;
        }
        PreprocessTreeNode cur = root;
        for (String pathNode : pathNodes) {
            if (cur.getChildren() != null && cur.getChildren().containsKey(pathNode)) {
                cur = cur.getChildren().get(pathNode);
            } else {
                return false;
            }
        }
        return true;
    }

    private void saveNewNodes(String key, Set<String> newPaths) {
        if (newPaths == null || newPaths.size() == 0) {
            return;
        }
        for (String newPath : newPaths) {
            MessagePreprocessDto dto = new MessagePreprocessDto();
            dto.setKey(key);
            dto.setPath(newPath);
            dto.setPublishDate(System.currentTimeMillis());
            messagePreprocessRepository.update(dto);
        }
    }
}
