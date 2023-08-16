package com.arextest.web.core.business;

import com.arextest.desensitization.extension.DataDesensitization;
import com.arextest.web.core.business.util.RemoteJarLoader;
import com.arextest.web.core.repository.DesensitizationJarRepository;
import com.arextest.web.model.dto.DesensitizationJarDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.security.SecureClassLoader;
import java.util.List;

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

    public List<DesensitizationJarDto> listAllJars() {
        return jarRepository.queryAll();
    }

    private void validateJar(String uri) throws DesensitizationJarValidationException {
        try {
            SecureClassLoader classLoader = RemoteJarLoader.loadJar(uri);
            List<DataDesensitization> service = RemoteJarLoader.loadService(DataDesensitization.class, classLoader);
            if (service.isEmpty()) {
                throw new DesensitizationJarValidationException(ErrType.SERVICE_NOTFOUND);
            } else if (service.size() != 1) {
                throw new DesensitizationJarValidationException(ErrType.SERVICE_OVERLOAD);
            }
        } catch (MalformedURLException e) {
            throw new DesensitizationJarValidationException(ErrType.URL_MALFORMED);
        }
    }
}
