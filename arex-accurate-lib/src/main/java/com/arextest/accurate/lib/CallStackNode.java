package com.arextest.accurate.lib;

import org.checkerframework.checker.units.qual.C;

import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

public class CallStackNode {
    private final StackTraceElement value;
    private final TreeMap<String, CallStackNode> children;

    public CallStackNode(StackTraceElement value) {
        this.value = value;
        this.children = new TreeMap<>();
    }

    public StackTraceElement getValue() {
        return value;
    }

    public void addChild(CallStackNode child) {
        children.put(StackElementToString(child.getValue()), child);
    }

    public CallStackNode addChild(StackTraceElement element) {
        CallStackNode newNode = new CallStackNode(element);
        children.put(StackElementToString(element), newNode);
        return newNode;
    }

    public TreeMap<String, CallStackNode> getChildren() {
        return children;
    }

    public CallStackNode searchChildNode(StackTraceElement element) {
        String key = StackElementToString(element);
        if (children.containsKey(key))
            return children.get(key);
        return null;
    }

    public static String StackElementToString(StackTraceElement element) {
        return element.getClassName() + ":" + element.getMethodName();
    }

    public static boolean stackElementEqual(StackTraceElement left, StackTraceElement right) {
        if (!left.getMethodName().equals(right.getMethodName()))
            return false;
        return left.getClassName().equals(right.getClassName());
    }

    /**
     * @param root   tree's root
     * @param inputs one stack
     */
    public static CallStackNode inputCallStackNode(CallStackNode root, StackTraceElement[] inputs) {
        if (inputs == null || inputs.length == 0)
            return root;

        // 第一个增加的调用栈, inputs.length - 1是getStackTrace函数,所以不要的
        if (root == null) {
            CallStackNode cursor = new CallStackNode(inputs[0]);
            root = cursor;
            for (int i = 1; i < inputs.length - 1; i++) {
                CallStackNode child = new CallStackNode(inputs[i]);
                cursor.addChild(child);
                cursor = child;
            }
            return root;
        }

        //根节点不一致,则直接返回,不增加了
        if (!stackElementEqual(root.getValue(), inputs[0]))
            return root;

        // 对输入挨个去看
        CallStackNode cursor = root;
        for (int i = 1; i < inputs.length - 1; i++) {
            CallStackNode qNode = cursor.searchChildNode(inputs[i]);
            if (qNode == null) {
                cursor = cursor.addChild(inputs[i]);
                continue;
            }
            cursor = qNode;
        }

        return root;
    }
}
