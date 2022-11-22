package com.arextest.report.core.repository.mongo;

import com.arextest.report.core.repository.LabelRepository;
import com.arextest.report.model.dao.mongodb.LabelCollection;
import com.arextest.report.model.dto.LabelDto;
import com.arextest.report.model.mapper.LabelMapper;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author b_yu
 * @since 2022/11/17
 */
@Slf4j
@Component
public class LabelRepositoryImpl implements LabelRepository {

    private static String WORKSPACE_ID = "workspaceId";

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public boolean saveLabel(LabelDto dto) {
        LabelCollection dao = LabelMapper.INSTANCE.daoFromDto(dto);
        try {
            mongoTemplate.save(dao);
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to save label.", e);
            return false;
        }
    }
    @Override
    public boolean removeLabel(String labelId) {
        Query query = Query.query(Criteria.where(DASH_ID).is(labelId));
        DeleteResult deleteResult = mongoTemplate.remove(query, LabelCollection.class);
        return deleteResult.getDeletedCount() > 0;
    }
    @Override
    public List<LabelDto> queryLabelsByWorkspaceId(String workspaceId) {
        Query query = Query.query(Criteria.where(WORKSPACE_ID).is(workspaceId));
        List<LabelCollection> daos = mongoTemplate.find(query, LabelCollection.class);
        return daos.stream().map(LabelMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }
}
