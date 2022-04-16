package io.arex.report.model.api.contracts.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class UnmatchedPairEntity {
    private int unmatchedType;
    private List<NodeEntity> leftUnmatchedPath;
    private List<NodeEntity> rightUnmatchedPath;
    private List<String> listKeys;
    private List<List<NodeEntity>> listKeyPath = new ArrayList<>();
    private Trace trace;
}
