package com.arextest.web.model.contract.contracts.common;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class UnmatchedPairEntity {
    private int unmatchedType;
    private List<NodeEntity> leftUnmatchedPath;
    private List<NodeEntity> rightUnmatchedPath;
    private List<String> listKeys;
    private List<List<NodeEntity>> listKeyPath = new ArrayList<>();
    private Trace trace;
}
