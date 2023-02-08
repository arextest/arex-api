package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.FSTraceLogRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.FSTraceLogCollection;
import com.arextest.web.model.dto.filesystem.FSTraceLogDto;
import com.arextest.web.model.mapper.FSTraceLogMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author b_yu
 * @since 2023/1/18
 */
@Component
public class FSTraceLogRepositoryImpl implements FSTraceLogRepository {

    private static String WORKSPACE_ID = "workspaceId";

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public boolean saveTraceLog(FSTraceLogDto traceLogDto) {
        FSTraceLogCollection dao = FSTraceLogMapper.INSTANCE.daoFromDto(traceLogDto);
        MongoHelper.initInsertObject(dao);
        dao = mongoTemplate.insert(dao);
        return StringUtils.isNotBlank(dao.getId());
    }
    @Override
    public FSTraceLogDto queryTraceLog(String id) {
        FSTraceLogCollection dao = mongoTemplate.findById(id, FSTraceLogCollection.class);
        return FSTraceLogMapper.INSTANCE.dtoFromDao(dao);
    }
    @Override
    public List<FSTraceLogDto> queryTraceLogsByWorkspaceId(String workspaceId) {
        Query query = Query.query(Criteria.where(WORKSPACE_ID).is(workspaceId));
        List<FSTraceLogCollection> fsTraceLogCollections = mongoTemplate.find(query, FSTraceLogCollection.class);
        return fsTraceLogCollections.stream().map(FSTraceLogMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }
}
