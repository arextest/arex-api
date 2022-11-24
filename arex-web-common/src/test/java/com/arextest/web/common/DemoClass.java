package com.arextest.web.common;

import lombok.Data;

import java.util.List;


@Data
public class DemoClass {
    private String name;
    private String address;
    private int age;
    List<SubDemoClass> subClasses;
}
