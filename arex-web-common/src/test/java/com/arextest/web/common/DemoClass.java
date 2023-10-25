package com.arextest.web.common;

import java.util.List;

import lombok.Data;

@Data
public class DemoClass {
    List<SubDemoClass> subClasses;
    private String name;
    private String address;
    private int age;
}
