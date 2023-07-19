package com.arextest.web.accurate.providers;

import com.arextest.web.accurate.model.CodeHostType;
import com.arextest.web.accurate.model.JavaCodeFile;

import java.util.List;

/**
 * Created by Qzmo on 2023/7/18
 */
public interface JavaCodeContentProvider {
    CodeHostType getCodeHostType();
    String getJavaCode(String projectPath, String filePath, String sha);
    List<JavaCodeFile> getModifiedFiles(String projectPath, String oldCommit, String newCommit);
}
