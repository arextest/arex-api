package com.arextest.accurate.biz;

import com.arextest.accurate.lib.JCodeMethod;
import com.arextest.accurate.lib.JavaProject;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class CodeAnalysis {
    public static boolean DEBUG = true;
    /**
     * 仅仅扫描源码, 产出类, 函数等信息
     *
     * @param gitAddr
     * @param branch
     */
    public static JavaProject ScanJava(String gitAddr, String branch) {
        JavaProject jProject = initJavaProject(gitAddr, branch,"");
        if (jProject == null)
            return null;

        try {
            jProject.scanProject();
            return jProject;
        } catch (Exception err) {
            LOGGER.error(err.toString());
        }
        return null;
    }

    /**
     * 查询所有的函数
     * @param gitAddr
     * @param branch
     * @return
     */
    public static List<JCodeMethod> ScanJavaMethodDefine(String gitAddr, String branch, String commitID) {
        JavaProject jProject = ScanJava(gitAddr, branch);
        if (jProject == null)
            return null;
        List<JCodeMethod> result = jProject.getJCodeMethods();
        return result;
    }

    /**
     * 查询变更的函数
     * @param gitAddr
     * @param branch
     * @param newCommit
     * @param oldCommit
     * @return
     */
    public static List<JCodeMethod> ScanChangedMethods(String gitAddr, String branch, String newCommit, String oldCommit) {
        JavaProject jProject = initJavaProject(gitAddr, branch, newCommit);
        if (jProject == null)
            return null;
        return jProject.scanCodeDiffMethods(newCommit, oldCommit);
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
            jProject.scanProject();
            jProject.compileProject();
            return jProject;
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        }

        return null;
    }

    /**
     * 初始化一个项目的代码库
     * @param gitAddr
     * @param branch
     * @param commitID
     * @return
     */
    public static JavaProject initJavaProject(String gitAddr, String branch,String commitID) {
        JavaProject jProject = new JavaProject();
        jProject.initialProjectConfig(gitAddr, branch);
        if (!jProject.initGitEnvironment(commitID)) {
            return null;
        }
        return jProject;
    }

}
