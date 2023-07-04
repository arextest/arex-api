package com.arextest.accurate.lib;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class DynamicTracing {
    private Set<String> methodVisitedSet = new HashSet<>();

    /**
     * 将所有的调用栈合并成为一个树
     * StackTraceElement[] [0]是调用栈顶,最后的调用,是getStackTrace()
     * [栈底]是根调用, 跟调用有很多是相同的,不需要跟进分析
     *
     * @param stacks StackTraceElement[][]
     */
    public String StackTracesToCallGraph(List<String> stacks, String nameSpacePrefix) {
        if (stacks == null || stacks.size() == 0 || StringUtils.isEmpty(nameSpacePrefix))
            return null;

        //GSON转换为StackTraceElement[], 并统计调用栈最高最低
        List<StackTraceElement[]> stackArray = new ArrayList<>();
        int maxDeep = 0;
        int minDeep = stacks.get(0).length();
        int maxPos = -1;
        int curPos = 0;
        for (String item : stacks) {
            StackTraceElement[] oneStack = new Gson().fromJson(item, StackTraceElement[].class);
            curPos++;
            if (oneStack == null)
                continue;
            if (oneStack.length > maxDeep) {
                maxDeep = oneStack.length;
                maxPos = curPos;
            }
            if (oneStack.length < minDeep)
                minDeep = oneStack.length;
            stackArray.add(oneStack);
        }

        // 寻找调用栈第一个开始存在差异的点,返回上一个共同节点, 如果碰到需要跟踪的代码,就算一样也返回
        int cursor;
        int diffPoint = -1;
        for (cursor = 1; cursor <= minDeep && diffPoint == -1; cursor++) {
            String className = stackArray.get(maxPos)[maxDeep - cursor].getClassName();
            String methodName = stackArray.get(maxPos)[maxDeep - cursor].getMethodName();
            for (StackTraceElement[] jItemStack : stackArray) {
                if (!className.equals(jItemStack[jItemStack.length - cursor].getClassName())
                        || !methodName.equals(jItemStack[jItemStack.length - cursor].getMethodName())) {
                    System.out.println("Diff Stack:" + className + ":" + methodName + "===>"
                            + jItemStack[jItemStack.length - cursor].getClassName() + ":"
                            + jItemStack[jItemStack.length - cursor].getMethodName());
                    diffPoint = cursor - 1;
                    break;
                }
            }
            if (className.startsWith(nameSpacePrefix)) {
                diffPoint = cursor;
                break;
            }
            System.out.println("Same Stack: " + className + ":" + methodName);
        }
        if (diffPoint == -1)
            diffPoint = curPos - 1;

        // 第二轮检查,只返回需要的
        List<StackTraceElement[]> callStacks = new LinkedList<>();
        for (StackTraceElement[] oneStack : stackArray) {
            int targetPos = oneStack.length - diffPoint;
            StackTraceElement[] stackSome = new StackTraceElement[targetPos + 1];
            for (int i = targetPos; i >= 0; i--) {
                stackSome[targetPos - i] = oneStack[i];
                if (oneStack[i].getClassName().startsWith(nameSpacePrefix)) {
                    String methodKey = oneStack[i].getClassName() + ":" + oneStack[i].getMethodName();
                    methodVisitedSet.add(methodKey);
                }
            }
            callStacks.add(stackSome);
        }

        // 第三轮检查, 将多个list合并到树, 返回JSON
        CallStackNode root = null;
        for (StackTraceElement[] oneStack : callStacks) {
            root = CallStackNode.inputCallStackNode(root, oneStack);
        }
        return new Gson().toJson(root);
    }
}
