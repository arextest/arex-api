package com.arextest.web.core.business;

import com.arextest.common.utils.PluginClassLoaderUtils;
import com.arextest.desensitization.extension.DataDesensitization;
import com.arextest.web.core.repository.DesensitizationJarRepository;
import com.arextest.web.model.contract.contracts.datadesensitization.DesensitizationJar;
import com.arextest.web.model.dto.DesensitizationJarDto;
import com.arextest.web.model.mapper.DesensitizationJarMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    public static class DesensitizationJarValidationException extends RuntimeException {
        public DesensitizationJarValidationException(ErrType errType) {
            super(errType.message);
        }
    }

    enum ErrType {
        URL_MALFORMED("Remote Jar Url is malformed"),
        SERVICE_NOTFOUND("Remote Jar can not load DataDesensitization interface implementation"),
        SERVICE_OVERLOAD("Remote Jar found more than one DataDesensitization interface implementation"),
        ;
        private final String message;

        ErrType(String message) {
            this.message = message;
        }
    }

    public void saveJar(String uri, String remark) throws DesensitizationJarValidationException {
        validateJar(uri);
        jarRepository.deleteAll();
        DesensitizationJarDto jarDto = new DesensitizationJarDto();
        jarDto.setJarUrl(uri);
        jarDto.setRemark(remark);
        jarRepository.saveJar(jarDto);
    }

    public void deleteJar(String jarId) {
        jarRepository.deleteJar(jarId);
    }

    public List<DesensitizationJar> listAllJars() {
        return jarRepository.queryAll().stream().map(DesensitizationJarMapper.INSTANCE::contractFromDto).collect(Collectors.toList());
    }

    private void validateJar(String uri) throws DesensitizationJarValidationException {
        if (!uri.startsWith("http")) {
            throw new DesensitizationJarValidationException(ErrType.URL_MALFORMED);
        }

        PluginClassLoaderUtils.loadJar(uri);
        List<DataDesensitization> service = PluginClassLoaderUtils.loadService(DataDesensitization.class);

        if (service.isEmpty()) {
            throw new DesensitizationJarValidationException(ErrType.SERVICE_NOTFOUND);
        } else if (service.size() != 1) {
            throw new DesensitizationJarValidationException(ErrType.SERVICE_OVERLOAD);
        }
    }
}
