package com.arextest.web.accurate.biz;

import com.arextest.web.accurate.lib.JavaProject;
import com.arextest.web.accurate.model.CallGraphResponse;
import com.arextest.web.accurate.model.MethodTracing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CallTrace {

    /**
     * 获取最新代码的Tracing
     * @param reposURL 仓库URL
     * @param branch 分支
     * @param className 类名
     * @param methodName 方法名
     * @return List跟踪数据
     */
    public static List<MethodTracing> doTracingGraph(String reposURL, String branch, String className, String methodName) {
        CodeAnalysis ca = new CodeAnalysis();
        JavaProject jProject = ca.compile(reposURL, branch,"");
        if (jProject == null) {
            return null;
        }

        return jProject.findCallGraph(className, methodName);
    }

    public static CallGraphResponse traceRequest(String reposURL, String branch, String className, String methodName) {
        try {
            List<MethodTracing> stacks = doTracingGraph(reposURL,branch,className,methodName);
            if (stacks == null) {
                return CallGraphResponse.exceptionResponse("Find call graph That failed.");
            }
            CallGraphResponse callGraphResponse = new CallGraphResponse();
            callGraphResponse.setResult("success");
            callGraphResponse.setStacks(stacks);
            return callGraphResponse;
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
            return CallGraphResponse.exceptionResponse(ex.toString());
        }
    }
}
