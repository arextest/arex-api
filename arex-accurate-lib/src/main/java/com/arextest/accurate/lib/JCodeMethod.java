package com.arextest.accurate.lib;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class JCodeMethod {
    String className;
    String classFullName;
    String simpleName;
    String fullName;
    String declare;
    String oldDeclare;
    String accessSpecifier;
    List<String> params = new ArrayList<>();
    List<String> annotations = new ArrayList<>();

    String comment;
    String javadoc;

    public static JCodeMethod covertToJCodeMethod(MethodDeclaration method) {
        JCodeMethod jMethod = new JCodeMethod();
        ClassOrInterfaceDeclaration classDeclaration = method.findAncestor(ClassOrInterfaceDeclaration.class).get();
        jMethod.setClassName(classDeclaration.getName().toString());
        if (classDeclaration.getFullyQualifiedName().isPresent())
            jMethod.setClassFullName(classDeclaration.getFullyQualifiedName().get());

        jMethod.setSimpleName(method.getName().toString());
        jMethod.setFullName(getMethodFullName(method));
        jMethod.setDeclare(method.toString());
        jMethod.setAccessSpecifier(method.getAccessSpecifier().asString());

        List<String> listAnno = new ArrayList<>();
        for (AnnotationExpr expr : method.getAnnotations()) {
            listAnno.add(expr.toString());
        }
        if (listAnno.size() > 0)
            jMethod.setAnnotations(listAnno);

        List<String> listParams = new ArrayList<>();
        for (Parameter oneParam : method.getParameters()) {
            listParams.add(oneParam.getType().toString());
        }
        if (listParams.size() > 0)
            jMethod.setParams(listParams);

        if (method.getComment().isPresent())
            jMethod.setComment(method.getComment().get().asString());
        else
            jMethod.setComment(null);
        if (method.getJavadoc().isPresent())
            jMethod.setJavadoc(method.getJavadoc().toString());
        else
            jMethod.setJavadoc(null);

        return jMethod;
    }

    public static String getMethodFullName(MethodDeclaration md) {
        if (md == null)
            return null;
        StringBuilder sb = new StringBuilder();
        sb.append(md.getName().toString());

        for (Parameter oneParam : md.getParameters()) {
            sb.append(":");
            sb.append(oneParam.getType().toString());
        }
        return sb.toString();
    }
}
