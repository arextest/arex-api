package com.arextest.accurate.lib;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;


/**
 * Java Code analysis
 * Java Class Code structure
 */
@Slf4j
public class JCodeClass {
    @Getter
    private final CompilationUnit compilationUnit;
    @Getter
    public ClassOrInterfaceDeclaration typeDeclaration;
    @Getter
    private final String fileName;
    @Getter
    private final String fullName;
    /**
     * 实现的接口列表
     */
    @Setter
    @Getter
    private HashSet<String> interfaces = new HashSet<>();
    @Getter
    private HashMap<String, MethodDeclaration> methodTable = new HashMap<>();


    public JCodeClass(CompilationUnit cu, String fName, ClassOrInterfaceDeclaration cls) throws Exception {
        fileName = fName;
        compilationUnit = cu;
        typeDeclaration = cls;
        fullName = generateClassKey();
    }

    public String generateClassKey() {
        Optional fullyQualifiedName = typeDeclaration.getFullyQualifiedName();
        if (fullyQualifiedName.isPresent())
            return (String) fullyQualifiedName.get();
        return "";
    }

    /**
     * 将List的数据合并进来
     * @param methodTables List表格,一般直接从JavaParser解析过来的
     */
    public void setMethodTable(List methodTables) {
        for (Object oneItem : methodTables) {
            MethodDeclaration method = (MethodDeclaration)oneItem;
            String keyName = JavaProject.generateMethodDeclarationKey(method);
            if (!methodTable.containsKey(keyName)){
                methodTable.put(keyName, method);
            }
        }
    }

    /**
     * 将HashMap数据合并进来
     * @param methodTables, Map, 是已经解析过的类的方发表
     */
    public void setMethodTable(HashMap<String,MethodDeclaration> methodTables) {
        for (String keyName : methodTables.keySet()) {
            if (!methodTable.containsKey(keyName)){
                methodTable.put(keyName, methodTables.get(keyName));
            }
        }
    }


    public String getParentName() {
        if (typeDeclaration.getExtendedTypes().size() > 0) {
            return typeDeclaration.getExtendedTypes().get(0).getNameWithScope();
        }

        return "";
    }

}
