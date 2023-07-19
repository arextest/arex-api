package com.arextest.web.accurate.providers;

import com.arextest.web.accurate.model.CodeHostType;
import com.arextest.web.accurate.model.JavaCodeFile;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.CompareResults;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Qzmo on 2023/7/18
 */
@Slf4j
@Component
public class GitlabRepoProvider implements JavaCodeContentProvider {
    private static final GitLabApi gitLabApi = new GitLabApi("http://git.dev.sh.ctripcorp.com", "FGx2sgW_4zsMAQojCfz5");

    @Override
    public CodeHostType getCodeHostType() {
        return CodeHostType.GITLAB;
    }

    @Override
    public String getJavaCode(String projectPath, String filePath, String sha) {
        try {
            return gitLabApi.getRepositoryFileApi().getFile(projectPath, filePath, sha).getDecodedContentAsString();
        } catch (GitLabApiException e) {
            LOGGER.error("get java code error", e);
            return null;
        }
    }

    @Override
    public List<JavaCodeFile> getModifiedFiles(String projectPath, String oldCommitSha, String newCommitSha) {
        try {
            CompareResults repoDiffResult = gitLabApi.getRepositoryApi().compare(projectPath, oldCommitSha, newCommitSha);
            List<JavaCodeFile> res = new ArrayList<>();
            repoDiffResult.getDiffs().forEach(diff -> {
                if (diff.getDeletedFile()) return;
                JavaCodeFile diffFile = new JavaCodeFile();
                diffFile.setNewFile(diff.getNewFile());
                diffFile.setFilePath(diff.getNewPath());
                res.add(diffFile);
            });
            return res;
        } catch (GitLabApiException e) {
            LOGGER.error("get modified files error", e);
            return Collections.emptyList();
        }
    }
}
