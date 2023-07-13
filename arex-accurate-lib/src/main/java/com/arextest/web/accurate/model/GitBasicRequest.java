package com.arextest.web.accurate.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.eclipse.jgit.revwalk.RevCommit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
public class GitBasicRequest {
    private String repositoryURL;
    private String branch;

    private String newCommit;
    private String oldCommit;

    private String beginDate;
    private String endDate;

    private String replayID;

    /**
     * 判断请求是否准备好(就是数据是否完整)
     * 为空,就是没有准备好
     *
     * @return true为没有准备好; false为准备好了
     */
    public boolean requestNotPrepared(String mode) {
        switch (mode.toLowerCase()) {
            case "clone":
                return repositoryURL.isBlank() || branch.isBlank();
            case "listCommit":
                return repositoryURL.isBlank() || newCommit.isBlank() || oldCommit.isBlank();
            case "date":
                return repositoryURL.isBlank() || beginDate == null || beginDate.isEmpty() || endDate.isBlank();
            case "replay":
                return repositoryURL.isBlank() || replayID.isBlank();
            case "commits":
            case "clean":
            default:
                return repositoryURL.isBlank();
        }
    }

    /**
     * 排序存在问题
     *
     * @param request
     * @param rcList
     * @return
     */
    public static ImmutablePair<String, String> getCommits(GitBasicRequest request, List<RevCommit> rcList) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date BeginTime = dateFormat.parse(request.getBeginDate());
        Date EndTime = new Date();
        if (request.getEndDate() != null)
            EndTime = dateFormat.parse(request.getEndDate());
        if (EndTime.before(BeginTime)) {
            Date tempTime = EndTime;
            EndTime = BeginTime;
            BeginTime = tempTime;
        }

        String newCommit = "";
        String oldCommit = "";
        if (rcList.size() == 0)
            return null;
//        rcList.sort(new Comparator<RevCommit>() {
//            @Override
//            public int compare(RevCommit o1, RevCommit o2) {
//                if (o1.getCommitTime() <= o2.getCommitTime())
//                    return -1;
//                else
//                    return 1;
//            }
//        });

        for (int i = 0; i < rcList.size(); i++) {
            if (rcList.get(i) == null)
                continue;
            Date rcDate = new Date(rcList.get(i).getCommitTime() * 1000L);
            if (rcDate.after(BeginTime)) {
                newCommit = rcList.get(i).getName();
                break;
            }
        }
        if (StringUtils.isEmpty(newCommit))
            return null;

        for (int i = rcList.size() - 1; i > 0; i--) {
            if (rcList.get(i) == null)
                continue;
            Date rcDate = new Date(rcList.get(i).getCommitTime() * 1000L);
            if (rcDate.before(EndTime)) {
                oldCommit = rcList.get(i).getName();
                break;
            }
        }

        return new ImmutablePair<>(newCommit, oldCommit);
    }
}
