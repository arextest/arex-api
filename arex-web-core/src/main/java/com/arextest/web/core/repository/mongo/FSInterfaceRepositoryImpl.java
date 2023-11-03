package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.FSInterfaceCollection;
import com.arextest.web.model.dao.mongodb.entity.AddressDao;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import com.arextest.web.model.dto.filesystem.FSItemDto;
import com.arextest.web.model.mapper.FSInterfaceMapper;
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
public class FSInterfaceRepositoryImpl implements FSInterfaceRepository {

  private static final String GET_METHOD = "GET";

  @Resource
  private MongoTemplate mongoTemplate;

  @Override
  public String initInterface(String parentId, Integer parentNodeType, String workspaceId,
      String name) {
    FSInterfaceCollection dao = new FSInterfaceCollection();
    MongoHelper.initInsertObject(dao);
    dao.setWorkspaceId(workspaceId);
    dao.setParentId(parentId);
    dao.setParentNodeType(parentNodeType);
    AddressDao addressDao = new AddressDao();
    addressDao.setMethod(GET_METHOD);
    dao.setAddress(addressDao);
    dao.setName(name);
    dao = mongoTemplate.insert(dao);
    return dao.getId();
  }

  @Override
  public Boolean removeInterface(String id) {
    Query query = Query.query(Criteria.where(DASH_ID).is(id));
    FSInterfaceCollection dao = mongoTemplate.findAndRemove(query, FSInterfaceCollection.class);
    return dao != null ? true : false;
  }

  @Override
  public Boolean removeInterfaces(Set<String> ids) {
    Set<ObjectId> objectIds = ids.stream().map(id -> new ObjectId(id)).collect(Collectors.toSet());
    Query query = Query.query(Criteria.where(DASH_ID).in(objectIds));
    DeleteResult result = mongoTemplate.remove(query, FSInterfaceCollection.class);
    return result.getDeletedCount() > 0;
  }

  @Override
  public FSInterfaceDto saveInterface(FSInterfaceDto interfaceDto) {
    if (StringUtils.isEmpty(interfaceDto.getId())) {
      FSInterfaceCollection dao = FSInterfaceMapper.INSTANCE.daoFromDto(interfaceDto);
      dao = mongoTemplate.save(dao);
      return FSInterfaceMapper.INSTANCE.dtoFromDao(dao);
    } else {
      Query query = Query.query(Criteria.where(DASH_ID).is(new ObjectId(interfaceDto.getId())));
      Update update = MongoHelper.getUpdate();

      FSInterfaceCollection dao = FSInterfaceMapper.INSTANCE.daoFromDto(interfaceDto);
      MongoHelper.appendFullProperties(update, dao);

      FSInterfaceCollection result = mongoTemplate.findAndModify(query, update,
          FindAndModifyOptions.options().returnNew(true), FSInterfaceCollection.class);

      return FSInterfaceMapper.INSTANCE.dtoFromDao(result);
    }
  }

  @Override
  public FSInterfaceDto queryInterface(String id) {
    FSInterfaceCollection dao = mongoTemplate.findById(new ObjectId(id),
        FSInterfaceCollection.class);
    if (dao == null) {
      return null;
    }
    return FSInterfaceMapper.INSTANCE.dtoFromDao(dao);
  }

  @Override
  public List<FSItemDto> queryInterfaces(Set<String> ids) {
    Set<ObjectId> objectIds = ids.stream().map(id -> new ObjectId(id)).collect(Collectors.toSet());
    Query query = Query.query(Criteria.where(DASH_ID).in(objectIds));
    List<FSInterfaceCollection> daos = mongoTemplate.find(query, FSInterfaceCollection.class);
    return daos.stream().map(FSInterfaceMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
  }

  @Override
  public String duplicate(FSInterfaceDto dto) {
    FSInterfaceCollection dao = FSInterfaceMapper.INSTANCE.daoFromDto(dto);
    MongoHelper.initInsertObject(dao);
    dao = mongoTemplate.insert(dao);
    return dao.getId();
  }
}
