package com.arextest.web.accurate.model;

import com.github.javaparser.ast.expr.MethodCallExpr;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class MethodTracing {
    private String mappingURL;
    private String methodName;
    private String className;
    // 存储有代码变更的方法清单
    private List<String> listCodeChangedMethods = new LinkedList<>();
    // 有源码跟踪的方法清单
    private List<String> listCodeMethods = new LinkedList<>();
    // 存储全量的访问清单
    private List<String> listFullMethodInvocations = new LinkedList<>();
    // 存储调用图
    private List<String> listTraceGraph = new LinkedList<>();
}
