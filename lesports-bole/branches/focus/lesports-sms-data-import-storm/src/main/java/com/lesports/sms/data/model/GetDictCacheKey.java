package com.lesports.sms.data.model;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/7/25
 */
public class GetDictCacheKey {
    private String partnerType;
    private String code;

    public GetDictCacheKey(String code, String partnerType) {
        this.code = code;
        this.partnerType = partnerType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GetDictCacheKey that = (GetDictCacheKey) o;

        if (partnerType != null ? !partnerType.equals(that.partnerType) : that.partnerType != null) return false;
        return !(code != null ? !code.equals(that.code) : that.code != null);

    }

    @Override
    public int hashCode() {
        int result = partnerType != null ? partnerType.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        return result;
    }

    public String getPartnerType() {
        return partnerType;
    }

    public void setPartnerType(String partnerType) {
        this.partnerType = partnerType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
