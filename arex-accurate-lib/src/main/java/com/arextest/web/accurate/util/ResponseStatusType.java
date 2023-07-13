package com.arextest.web.accurate.util;

public class ResponseStatusType {
    private Integer responseCode;
    private String responseDesc;
    private Long timestamp;

    public boolean hasError() {
        return this.responseCode == null || this.responseCode != ResponseCode.SUCCESS.getCodeValue();
    }

    public ResponseStatusType() {
    }

    public Integer getResponseCode() {
        return this.responseCode;
    }

    public String getResponseDesc() {
        return this.responseDesc;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public void setResponseDesc(String responseDesc) {
        this.responseDesc = responseDesc;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ResponseStatusType)) {
            return false;
        } else {
            ResponseStatusType other = (ResponseStatusType)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label47: {
                    Object this$responseCode = this.getResponseCode();
                    Object other$responseCode = other.getResponseCode();
                    if (this$responseCode == null) {
                        if (other$responseCode == null) {
                            break label47;
                        }
                    } else if (this$responseCode.equals(other$responseCode)) {
                        break label47;
                    }

                    return false;
                }

                Object this$responseDesc = this.getResponseDesc();
                Object other$responseDesc = other.getResponseDesc();
                if (this$responseDesc == null) {
                    if (other$responseDesc != null) {
                        return false;
                    }
                } else if (!this$responseDesc.equals(other$responseDesc)) {
                    return false;
                }

                Object this$timestamp = this.getTimestamp();
                Object other$timestamp = other.getTimestamp();
                if (this$timestamp == null) {
                    if (other$timestamp != null) {
                        return false;
                    }
                } else if (!this$timestamp.equals(other$timestamp)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ResponseStatusType;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $responseCode = this.getResponseCode();
        result = result * 59 + ($responseCode == null ? 43 : $responseCode.hashCode());
        Object $responseDesc = this.getResponseDesc();
        result = result * 59 + ($responseDesc == null ? 43 : $responseDesc.hashCode());
        Object $timestamp = this.getTimestamp();
        result = result * 59 + ($timestamp == null ? 43 : $timestamp.hashCode());
        return result;
    }

    public String toString() {
        return "ResponseStatusType(responseCode=" + this.getResponseCode() + ", responseDesc=" + this.getResponseDesc() + ", timestamp=" + this.getTimestamp() + ")";
    }
}
