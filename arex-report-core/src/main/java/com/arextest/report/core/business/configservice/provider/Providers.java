package com.arextest.report.core.business.configservice.provider;

import com.arextest.report.model.api.contracts.common.enums.ApplicationServiceOperationType;
import com.arextest.report.model.api.contracts.configservice.application.ApplicationConfiguration;
import com.arextest.report.model.api.contracts.configservice.application.ApplicationDescription;
import com.arextest.report.model.api.contracts.configservice.application.OperationDescription;
import com.arextest.report.model.api.contracts.configservice.application.ServiceDescription;
import com.arextest.report.model.api.contracts.configservice.application.provider.ApplicationDescriptionProvider;
import com.arextest.report.model.api.contracts.configservice.application.provider.ApplicationServiceDescriptionProvider;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author jmo
 * @since 2022/2/21
 */
public final class Providers {
    private Providers() {
    }

    public static ApplicationDescriptionProvider createApplication(String name) {
        if (StringUtils.isEmpty(name)) {
            return new UnknownApplicationDescriptionImpl();
        }
        return lookup(ApplicationDescriptionProvider.class, name);
    }

    public static ApplicationServiceDescriptionProvider createApplicationService(String name) {
        if (StringUtils.isEmpty(name)) {
            return new DefaultServiceDescriptionProviderImpl();
        }
        return lookup(ApplicationServiceDescriptionProvider.class, name);
    }

    private static <T> T lookup(Class<T> tClass, String name) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(tClass);
        Iterator<T> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            T instance = iterator.next();
            if (instance.getClass().getSimpleName().equals(name)) {
                return instance;
            }
        }
        return null;
    }

    private static final class DefaultServiceDescriptionProviderImpl implements ApplicationServiceDescriptionProvider {

        @Override
        public List<? extends ServiceDescription> get(String appId, String host) {
            DefaultServiceDescriptionImpl serviceConfiguration = new DefaultServiceDescriptionImpl();
            serviceConfiguration.setAppId(appId);
            serviceConfiguration.setServiceName("unknown service name");
            serviceConfiguration.setServiceKey("unknown service key");
            return Collections.singletonList(serviceConfiguration);
        }

        @Data
        private static final class DefaultServiceDescriptionImpl implements ServiceDescription {
            private String serviceCode;
            private String serviceName;
            private String serviceKey;
            private List<? extends OperationDescription> operationList;
            private String appId;
        }
    }

    private static final class SpringActuatorMappingProviderImpl implements ApplicationServiceDescriptionProvider {
        private static final RestTemplate restTemplate = new RestTemplate();

        @Override
        public List<? extends ServiceDescription> get(String appId, String host) {
            ActuatorServiceDescriptionImpl serviceConfiguration = new ActuatorServiceDescriptionImpl();
            serviceConfiguration.setAppId(appId);
            serviceConfiguration.setServiceName("unknown service name");
            serviceConfiguration.setServiceKey("unknown service key");
            String url = "http://" + host + "/actuator/mappings";
            List<OperationDescription> operationDescriptionList = getOperationDescriptionList(url);
            serviceConfiguration.setOperationList(operationDescriptionList);
            return Collections.singletonList(serviceConfiguration);
        }

        private List<OperationDescription> getOperationDescriptionList(String url) {
            ActuatorResponse actuatorResponse = restTemplate.getForObject(url, ActuatorResponse.class);
            if (actuatorResponse == null) {
                return Collections.emptyList();
            }
            ActuatorContext actuatorContext = actuatorResponse.getContexts();
            ActuatorApplication actuatorApplication = actuatorContext.getApplication();
            ApplicationMapping applicationMapping = actuatorApplication.getMappings();
            DispatcherServlets dispatcherServlets = applicationMapping.getDispatcherServlets();
            List<DispatcherServlet> dispatcherServletList = dispatcherServlets.getDispatcherServlet();
            OperationDescription description;
            DispatcherDetails dispatcherDetails;
            Set<OperationDescription> operationDescriptionSet = new HashSet<>(dispatcherServletList.size());
            for (DispatcherServlet dispatcherServlet : dispatcherServletList) {
                dispatcherDetails = dispatcherServlet.getDetails();
                if (dispatcherDetails == null) {
                    continue;
                }
                description = dispatcherDetails.getRequestMappingConditions();
                if (description == null) {
                    continue;
                }
                operationDescriptionSet.add(description);
            }
            return new ArrayList<>(operationDescriptionSet);
        }

        @Data
        private static final class ActuatorServiceDescriptionImpl implements ServiceDescription {
            private String serviceCode;
            private String serviceName;
            private String serviceKey;
            private List<? extends OperationDescription> operationList;
            private String appId;
        }

        @Data
        private final static class ActuatorResponse {
            private ActuatorContext contexts;
        }

        @Data
        private final static class ActuatorContext {
            private ActuatorApplication application;
        }

        @Data
        private final static class ActuatorApplication {
            private ApplicationMapping mappings;
        }

        @Data
        private final static class DispatcherServlets {
            private List<DispatcherServlet> dispatcherServlet;
        }

        @Data
        private final static class ApplicationMapping {
            private DispatcherServlets dispatcherServlets;
        }

        @Data
        private final static class DispatcherServlet {
            private DispatcherDetails details;
        }

        @Data
        private final static class DispatcherDetails {
            private RequestMappingConditions requestMappingConditions;
        }

        @Getter
        @Setter
        private final static class RequestMappingConditions implements OperationDescription {
            private List<String> patterns;

            @Override
            public String getOperationName() {
                return patterns.get(0);
            }

            @Override
            public int getOperationType() {
                return ApplicationServiceOperationType.HTTP_SERVLET_SERVICE.getCodeValue();
            }

            @Override
            public int hashCode() {
                return this.getOperationName().hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof RequestMappingConditions) {
                    return this.getOperationName().equals(((RequestMappingConditions) obj).getOperationName());
                }
                return true;
            }
        }
    }

    private static final class UnknownApplicationDescriptionImpl implements ApplicationDescriptionProvider {
        @Override
        public ApplicationDescription get(String appId) {
            ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();
            applicationConfiguration.setAppId(appId);
            applicationConfiguration.setOwner("unknown owner");
            applicationConfiguration.setAppName("unknown app name");
            applicationConfiguration.setOrganizationName("unknown organization name");
            applicationConfiguration.setGroupName("unknown group name");
            applicationConfiguration.setGroupId("unknown group id");
            applicationConfiguration.setOrganizationId("unknown organization id");
            applicationConfiguration.setDescription("unknown description");
            applicationConfiguration.setCategory("unknown category");
            return applicationConfiguration;
        }
    }
}
