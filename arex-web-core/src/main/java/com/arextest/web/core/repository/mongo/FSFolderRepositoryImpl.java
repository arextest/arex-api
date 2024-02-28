package com.arextest.web.core.repository.mongo;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.FSFolderRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.FSFolderCollection;
import com.arextest.web.model.dto.filesystem.FSFolderDto;
import com.arextest.web.model.dto.filesystem.FSItemDto;
import com.arextest.web.model.mapper.FSFolderMapper;
import com.mongodb.client.result.DeleteResult;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FSFolderRepositoryImpl implements FSFolderRepository {

  @Resource
  private MongoTemplate mongoTemplate;

  @Override
  public String initFolder(String parentId, Integer parentNodeType, String workspaceId,
      String name) {
    FSFolderCollection dao = new FSFolderCollection();
    MongoHelper.initInsertObject(dao);
    dao.setParentId(parentId);
    dao.setParentNodeType(parentNodeType);
    dao.setWorkspaceId(workspaceId);
    dao.setName(name);
    dao = mongoTemplate.insert(dao);
    return dao.getId();
  }

  @Override
  public Boolean removeFolder(String id) {
    Query query = Query.query(Criteria.where(DASH_ID).is(id));
    try {
      mongoTemplate.findAndRemove(query, FSFolderCollection.class);
      return true;
    } catch (Exception e) {
      LogUtils.error(LOGGER, "failed to remove folder.", e);
      return false;
    }

  }

  @Override
  public Boolean removeFolders(Set<String> ids) {
    Set<ObjectId> objectIds = ids.stream().map(id -> new ObjectId(id)).collect(Collectors.toSet());
    Query query = Query.query(Criteria.where(DASH_ID).in(objectIds));
    DeleteResult result = mongoTemplate.remove(query, FSFolderCollection.class);
    return result.getDeletedCount() > 0;
  }

  @Override
  public FSFolderDto queryById(String id) {
    FSFolderCollection dao = mongoTemplate.findById(new ObjectId(id), FSFolderCollection.class);
    return FSFolderMapper.INSTANCE.dtoFromDao(dao);
  }

  @Override
  public String duplicate(FSFolderDto dto) {
    FSFolderCollection dao = FSFolderMapper.INSTANCE.daoFromDto(dto);
    MongoHelper.initInsertObject(dao);
    dao = mongoTemplate.insert(dao);
    return dao.getId();
  }

  @Override
  public List<FSItemDto> queryByIds(List<String> ids) {
    List<ObjectId> objectIds = ids.stream().map(id -> new ObjectId(id))
        .collect(Collectors.toList());
    Query query = Query.query(Criteria.where(DASH_ID).in(objectIds));
    List<FSFolderCollection> results = mongoTemplate.find(query, FSFolderCollection.class);
    return results.stream().map(FSFolderMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
  }

  @Override
  public FSFolderDto saveFolder(FSFolderDto dto) {
    if (StringUtils.isEmpty(dto.getId())) {
      FSFolderCollection dao = FSFolderMapper.INSTANCE.daoFromDto(dto);
      dao = mongoTemplate.save(dao);
      return FSFolderMapper.INSTANCE.dtoFromDao(dao);
    } else {
      Query query = Query.query(Criteria.where(DASH_ID).is(new ObjectId(dto.getId())));
      Update update = MongoHelper.getUpdate();

      FSFolderCollection dao = FSFolderMapper.INSTANCE.daoFromDto(dto);
      MongoHelper.appendFullProperties(update, dao);

      FSFolderCollection result = mongoTemplate.findAndModify(query, update,
          FindAndModifyOptions.options().returnNew(true), FSFolderCollection.class);

      return FSFolderMapper.INSTANCE.dtoFromDao(result);
    }

  }

  @Override
  public List<FSFolderDto> queryFolders(String workspaceId, String name, List<String> includeLabels,
      List<String> excludeLabels, Integer pageSize) {
    Query query = Query.query(
        Criteria.where(FSFolderCollection.Fields.workspaceId).is(workspaceId));
    if (StringUtils.isNotEmpty(name)) {
      query.addCriteria(Criteria.where(FSFolderCollection.Fields.name).regex(name));
    }
    if (pageSize != null) {
      query.limit(pageSize);
    }
    List<FSFolderCollection> results = mongoTemplate.find(query, FSFolderCollection.class);
    return results.stream().map(FSFolderMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
  }

  @Override
  public List<FSItemDto> queryByIdsByParentIds(List<String> parentIds) {
    Query query = Query.query(Criteria.where(FSFolderCollection.Fields.parentId).in(parentIds));
    List<FSFolderCollection> results = mongoTemplate.find(query, FSFolderCollection.class);
    return results.stream().map(FSFolderMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
  }
}
