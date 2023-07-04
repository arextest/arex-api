package com.arextest.accurate.lib;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Slf4j
@Data
public class CodeDiff {
    String fileName;
    String fileFullName;
    List<LineRange> editedLineRangeList = new ArrayList<>();
    String content;
    String oldContent;

    public CodeDiff(String fileName, String fileFullName) {
        this.fileName = fileName;
        this.fileFullName = fileFullName;
    }

    public void appendRange(int lineBegin, int lineEnd, String diffLogs) {
        editedLineRangeList.add(new LineRange(lineBegin, lineEnd, diffLogs));
    }

    public boolean inRange(int lineBegin, int lineEnd) {
        for (LineRange oneRange : editedLineRangeList) {
            if (oneRange.inRange(lineBegin, lineEnd))
                LOGGER.info(fileFullName + ":" + oneRange.logs);
                return true;
        }
        return false;
    }
}
