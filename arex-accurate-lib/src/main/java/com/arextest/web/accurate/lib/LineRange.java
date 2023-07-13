package com.arextest.web.accurate.lib;

import lombok.Data;

@Data
public class LineRange {
    int lineBegin;
    int lineEnd;
    String logs;

    public LineRange(int lineBegin, int lineEnd, String diffLogs) {
        if (lineBegin>lineEnd){
            int max = lineBegin;
            lineBegin = lineEnd;
            lineEnd = max;
        }
        this.lineBegin = lineBegin;
        this.lineEnd = lineEnd;
        this.logs = diffLogs;
    }

    public boolean inRange(int rangeBegin, int rangeEnd){
        if (rangeBegin>rangeEnd){
            int max = rangeBegin;
            rangeBegin = rangeEnd;
            rangeEnd = max;
        }

        if ((rangeEnd < lineBegin) || (rangeBegin > lineEnd))
            return false;
        else
            return true;
    }
}
