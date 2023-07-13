package com.arextest.web.model.dao.mongodb;

import com.arextest.web.accurate.lib.JCodeMethod;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@NoArgsConstructor
@Document(collection = "MethodsInCommit")
public class MethodChangeInCommitCollection extends ModelBase {
    @NonNull
    private String url;
    private String branch;
    private String commitId;
    private List<JCodeMethod> methods;
}
