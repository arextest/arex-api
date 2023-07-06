package com.arextest.web.core.business.appcontract;

import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.model.contract.contracts.appcontract.AddDependencyToSystemRequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arextest.web.model.contract.contracts.appcontract.AddDependencyToSystemResponseType;

import javax.annotation.Resource;

@Component
public class AppContractService {

    @Resource
    AppContractRepository appContractRepository;
    public AddDependencyToSystemResponseType addDependencyToSystem(AddDependencyToSystemRequestType request) {
        AddDependencyToSystemResponseType response = new AddDependencyToSystemResponseType();
        return response;
    }

}
