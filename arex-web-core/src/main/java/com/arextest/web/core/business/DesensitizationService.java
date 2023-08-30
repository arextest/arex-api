package com.arextest.web.core.business;

import com.arextest.common.model.classloader.RemoteJarClassLoader;
import com.arextest.common.utils.RemoteJarLoaderUtils;
import com.arextest.extension.desensitization.DataDesensitization;
import com.arextest.web.core.repository.DesensitizationJarRepository;
import com.arextest.web.model.contract.contracts.datadesensitization.DesensitizationJar;
import com.arextest.web.model.dto.DesensitizationJarDto;
import com.arextest.web.model.mapper.DesensitizationJarMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Qzmo on 2023/8/16
 */
@Service
@Slf4j
public class DesensitizationService {
    @Resource
    private DesensitizationJarRepository jarRepository;

    public boolean saveJar(String uri, String remark) {
        jarRepository.deleteAll();
        DesensitizationJarDto jarDto = new DesensitizationJarDto();
        jarDto.setJarUrl(uri);
        jarDto.setRemark(remark);
        return jarRepository.saveJar(jarDto);
    }

    public boolean deleteJar(String jarId) {
        return jarRepository.deleteJar(jarId);
    }

    public List<DesensitizationJar> listAllJars() {
        return jarRepository.queryAll().stream().map(DesensitizationJarMapper.INSTANCE::contractFromDto).collect(Collectors.toList());
    }
}
