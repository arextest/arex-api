package com.arextest.web.core.business.filesystem.recovery;

import com.arextest.web.core.business.filesystem.FileSystemUtils;
import com.arextest.web.core.business.filesystem.ItemInfo;
import com.arextest.web.core.business.filesystem.ItemInfoFactory;
import com.arextest.web.core.repository.FSTreeRepository;
import com.arextest.web.model.dto.filesystem.FSNodeDto;
import com.arextest.web.model.dto.filesystem.FSTraceLogDto;
import com.arextest.web.model.dto.filesystem.FSTreeDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author b_yu
 * @since 2023/2/7
 */
@Component("Recovery-2")
public class UpdateItemRecoveryServiceImpl implements RecoveryService {
    @Resource
    private FSTreeRepository fsTreeRepository;

    @Resource
    private FileSystemUtils fileSystemUtils;

    @Resource
    private ItemInfoFactory itemInfoFactory;

    @Override
    public boolean recovery(FSTraceLogDto log) {
        FSTreeDto fsTreeDto = fsTreeRepository.queryFSTreeById(log.getWorkspaceId());
        FSNodeDto nodeDto = fileSystemUtils.deepFindByInfoId(fsTreeDto.getRoots(), log.getInfoId());
        if (nodeDto == null) {
            return false;
        }

        if (CollectionUtils.isEmpty(log.getItems())) {
            return false;
        }
        ItemInfo itemInfo = itemInfoFactory.getItemInfo(nodeDto.getNodeType());

        itemInfo.saveItem(log.getItems().get(0));
        return true;
    }
}
