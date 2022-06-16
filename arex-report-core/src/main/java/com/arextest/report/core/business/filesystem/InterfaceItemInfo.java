package com.arextest.report.core.business.filesystem;

import com.arextest.report.core.repository.FSInterfaceRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("1")
public class InterfaceItemInfo implements ItemInfo {

    @Resource
    private FSInterfaceRepository fsInterfaceRepository;

    @Override
    public String saveItem() {
        return fsInterfaceRepository.initInterface();
    }
    @Override
    public Boolean removeItem(String infoId) {
        return fsInterfaceRepository.removeInterface(infoId);
    }
}
