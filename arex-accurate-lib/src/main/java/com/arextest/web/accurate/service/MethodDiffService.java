package com.arextest.web.accurate.service;

import com.arextest.web.accurate.differs.MethodDifferImpl;
import com.arextest.web.accurate.model.BaseDiffReq;
import com.arextest.web.accurate.model.BaseDiffRes;
import com.arextest.web.accurate.model.JavaCodeFile;
import com.arextest.web.accurate.providers.JavaCodeContentProvider;
import com.arextest.web.accurate.providers.ProviderFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by Qzmo on 2023/7/18
 */
@Service
public class MethodDiffService {

    @Resource
    ProviderFactory providerFactory;

    public Map<String, Set<String>> listDiffMethods(@RequestBody BaseDiffReq request) {
        JavaCodeContentProvider provider = providerFactory.pick(request.getHostEnum());
        List<JavaCodeFile> modifiedFiles = provider.getModifiedFiles(request.getRepoPath(), request.getOldCommitSha(), request.getNewCommitSha());
        Map<String, Set<String>> diffMethods = new HashMap<>();

        for (JavaCodeFile modifiedFile : modifiedFiles) {
            // todo handle new file
            if (modifiedFile.isNewFile()) {
                continue;
            }

            if (!modifiedFile.getFilePath().endsWith(".java")) {
                continue;
            }

            Optional<String> oldFile = provider.getJavaCode(request.getRepoPath(), modifiedFile.getFilePath(), request.getOldCommitSha());
            Optional<String> newFile = provider.getJavaCode(request.getRepoPath(), modifiedFile.getFilePath(), request.getNewCommitSha());

            Set<String> diffMethodsOfFile = new MethodDifferImpl().diff(oldFile.orElse(""), newFile.orElse(""));
            if (!diffMethodsOfFile.isEmpty()) {
                diffMethods.put(modifiedFile.getFilePath(), diffMethodsOfFile);
            }
        }
        return diffMethods;
    }
}
