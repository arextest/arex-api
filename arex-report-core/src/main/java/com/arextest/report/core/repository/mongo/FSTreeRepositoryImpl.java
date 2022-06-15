package com.arextest.report.core.repository.mongo;

import com.arextest.report.core.repository.FSTreeRepository;
import com.arextest.report.model.dao.mongodb.FSTreeCollection;
import com.arextest.report.model.dto.FSTreeDto;
import com.arextest.report.model.dto.WorkspaceDto;
import com.arextest.report.model.mapper.FSTreeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FSTreeRepositoryImpl implements FSTreeRepository {
    private static final String DASH_ID = "_id";
    private static final String WORKSPACE_NAME = "workspaceName";
    private static final String USERNAME = "userName";
    private static final String ROOTS = "roots";

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public FSTreeDto initFSTree(FSTreeDto dto) {
        FSTreeCollection dao = FSTreeMapper.INSTANCE.daoFromDto(dto);
        ArexUpdate.initInsertObject(dao);
        FSTreeCollection result = mongoTemplate.insert(dao);
        return FSTreeMapper.INSTANCE.dtoFromDao(result);
    }

    @Override
    public FSTreeDto updateFSTree(FSTreeDto dto) {

        Update update = ArexUpdate.getUpdate();
        update.setOnInsert(WORKSPACE_NAME, dto.getWorkspaceName());
        update.setOnInsert(USERNAME, dto.getUserName());
        update.set(ROOTS, dto.getRoots());

        Query query = Query.query(Criteria.where(DASH_ID).is(dto.getId()));

        FSTreeCollection dao = mongoTemplate.findAndModify(query,
                update,
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                FSTreeCollection.class);

        return FSTreeMapper.INSTANCE.dtoFromDao(dao);
    }

    @Override
    public FSTreeDto queryFSTreeById(String id) {
        Query query = Query.query(Criteria.where(DASH_ID).is(id));
        FSTreeCollection dao = mongoTemplate.findOne(query, FSTreeCollection.class);
        return FSTreeMapper.INSTANCE.dtoFromDao(dao);
    }

    @Override
    public List<WorkspaceDto> queryWorkspacesByUser(String userName) {
        Query query = Query.query(Criteria.where(USERNAME).is(userName));
        Field field = query.fields();
        field.include(DASH_ID).include(WORKSPACE_NAME);
        List<FSTreeCollection> trees = mongoTemplate.find(query, FSTreeCollection.class);

        if (trees == null) {
            return null;
        }
        List<WorkspaceDto> workspaces = new ArrayList<>();
        trees.forEach(tree -> {
            WorkspaceDto dto = new WorkspaceDto();
            dto.setId(tree.getId());
            dto.setWorkspaceName(tree.getWorkspaceName());
            workspaces.add(dto);
        });
        return workspaces;
    }
}
