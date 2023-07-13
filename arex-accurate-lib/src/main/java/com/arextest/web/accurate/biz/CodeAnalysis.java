package com.arextest.web.accurate.biz;

import com.arextest.web.accurate.lib.JCodeMethod;
import com.arextest.web.accurate.lib.JavaProject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CodeAnalysis {
    /**
     * 仅仅扫描源码, 产出类, 函数等信息
     *
     * @param gitAddr  git url
     * @param branch   branch string
     */
    public static JavaProject ScanJava(JavaProject javaProject, String gitAddr, String branch) {
        try {
            javaProject.scanWholeProject();
            return javaProject;
        } catch (Exception err) {
            LOGGER.error(err.toString());
        }
        return null;
    }


    /**
     * 1. git pull code
     * 2. compile
     * 3. log all methods and classes
     *
     * @param gitAddr git http url
     * @param branch  branch name e.g. main, release
     * @return boolean if success return true, otherwise return false.
     */
    public JavaProject compile(String gitAddr, String branch,String commitID) {
        JavaProject jProject = initJavaProject(gitAddr, branch,commitID);
        if (jProject == null)
            return null;

        try {
            jProject.scanWholeProject();
            jProject.compileProject();
            return jProject;
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        }

        return null;
    }

    /**
     * 查询变更的函数
     * @param gitAddr git https url
     * @param branch  branch string
     * @param newCommit  commit id string begin
     * @param oldCommit  commit id string latest
     * @return list of JCodeMethod
     */
    public static List<JCodeMethod> ScanChangedMethods(String gitAddr, String branch, String newCommit, String oldCommit) {
        JavaProject jProject = initJavaProject(gitAddr, branch, newCommit);
        if (jProject == null)
            return null;
        return jProject.scanCodeDiffMethods(newCommit, oldCommit);
    }

    /**
     * 初始化一个项目的代码库
     * @param gitAddr  git url
     * @param branch   branch string
     * @param commitID commit id string
     * @return java project
     */
    public static JavaProject initJavaProject(String gitAddr, String branch, String commitID) {
        JavaProject jProject = new JavaProject();
        jProject.setupJavaProject(gitAddr, branch);
        return jProject;
    }

}
