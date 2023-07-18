package com.arextest.web.accurate.differs;

import java.util.Set;

/**
 * Created by Qzmo on 2023/7/18
 */
public interface MethodDiffer {
    /**
     * @param oldFile old .java file
     * @param newFile new .java file
     * @return A Set of method names that have been modified between oldFile and newFile
     */
    Set<String> diff(String oldFile, String newFile);
}
