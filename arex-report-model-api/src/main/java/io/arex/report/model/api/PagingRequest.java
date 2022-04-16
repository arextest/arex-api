package io.arex.report.model.api;


public interface PagingRequest {
    Integer getPageSize();
    void setPageSize(Integer pageSize);
    Integer getPageIndex();
    void setPageIndex(Integer pageIndex);
    Boolean getNeedTotal();
    void setNeedTotal(Boolean needTotal);

    
    default boolean checkPaging() {
        if (this.getPageIndex() == null) {
            return false;
        }
        if (this.getPageSize() == null || this.getPageSize() == 0) {
            return false;
        }
        if (this.getPageSize() > 100) {
            return false;
        }
        return true;
    }
}
