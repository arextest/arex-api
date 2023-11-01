package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.UserWorkspaceRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.UserWorkspaceCollection;
import com.arextest.web.model.dto.filesystem.UserWorkspaceDto;
import com.arextest.web.model.enums.InvitationType;
import com.arextest.web.model.mapper.UserWorkspaceMapper;
import com.mongodb.client.result.DeleteResult;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserWorkspaceRepositoryImpl implements UserWorkspaceRepository {

  private static final String USER_NAME = "userName";
  private static final String WORKSPACE_ID = "workspaceId";
  private static final String TOKEN = "token";
  private static final String STATUS = "status";

  @Resource
  private MongoTemplate mongoTemplate;

  @Override
  public UserWorkspaceDto queryUserWorkspace(String userName, String workspaceId) {
    Query query = Query.query(
        Criteria.where(USER_NAME).is(userName).and(WORKSPACE_ID).is(workspaceId));
    query.fields().exclude(TOKEN);
    UserWorkspaceCollection dao = mongoTemplate.findOne(query, UserWorkspaceCollection.class);
    return UserWorkspaceMapper.INSTANCE.dtoFromDao(dao);
  }

  @Override
  public UserWorkspaceDto update(UserWorkspaceDto dto) {
    Query query =
        Query.query(Criteria.where(USER_NAME).is(dto.getUserName()).and(WORKSPACE_ID)
            .is(dto.getWorkspaceId()));
    Update update = MongoHelper.getUpdate();
    MongoHelper.appendFullProperties(update, dto);

    UserWorkspaceCollection dao = mongoTemplate.findAndModify(query, update,
        FindAndModifyOptions.options().returnNew(true).upsert(true), UserWorkspaceCollection.class);
    return UserWorkspaceMapper.INSTANCE.dtoFromDao(dao);
  }

  @Override
  public Boolean verify(UserWorkspaceDto dto) {
    Query query = Query.query(Criteria.where(USER_NAME).is(dto.getUserName()).and(WORKSPACE_ID)
        .is(dto.getWorkspaceId()).and(TOKEN).is(dto.getToken()).and(DATA_CHANGE_UPDATE_TIME)
        .gt(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));
    return mongoTemplate.exists(query, UserWorkspaceCollection.class);
  }

  @Override
  public List<UserWorkspaceDto> queryWorkspacesByUser(String userName) {
    Query query = Query.query(
        Criteria.where(USER_NAME).is(userName).and(STATUS).is(InvitationType.INVITED));
    query.fields().exclude(TOKEN).exclude(STATUS).exclude(USER_NAME);
    List<UserWorkspaceCollection> workspaceDaos = mongoTemplate.find(query,
        UserWorkspaceCollection.class);
    if (workspaceDaos == null) {
      return null;
    }
    return workspaceDaos.stream().map(UserWorkspaceMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public List<UserWorkspaceDto> queryUsersByWorkspace(String workspaceId) {
    Query query = Query.query(Criteria.where(WORKSPACE_ID).is(workspaceId));
    query.fields().exclude(TOKEN);
    List<UserWorkspaceCollection> workspaceDaos = mongoTemplate.find(query,
        UserWorkspaceCollection.class);
    if (workspaceDaos == null) {
      return null;
    }
    return workspaceDaos.stream().map(UserWorkspaceMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public Boolean remove(String userName, String workspaceId) {
    Query query = Query.query(
        Criteria.where(USER_NAME).is(userName).and(WORKSPACE_ID).is(workspaceId));
    DeleteResult result = mongoTemplate.remove(query, UserWorkspaceCollection.class);
    return result.getDeletedCount() > 0;
  }

  @Override
  public Boolean removeByWorkspaceId(String workspaceId) {
    Query query = Query.query(Criteria.where(WORKSPACE_ID).is(workspaceId));
    DeleteResult result = mongoTemplate.remove(query, UserWorkspaceCollection.class);
    return result.getDeletedCount() > 0;
  }
}
