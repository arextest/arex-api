package io.arex.report.model.api.contracts;

import io.arex.report.model.api.contracts.common.Scene;
import lombok.Data;

import java.util.List;


@Data
public class QueryScenesResponseType {

    private List<Scene> scenes;
}
