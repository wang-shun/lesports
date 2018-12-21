package com.lesports.sms.data.model.olympic;

import com.lesports.utils.excel.annotation.Column;
import com.lesports.utils.excel.annotation.Sheet;

import java.io.Serializable;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-25
 */
@Sheet("Sheet1")
public class CommonCode implements Serializable {

    @Column(0)
    private String code;
    @Column(1)
    private String codeDEsc;
    @Column(2)
    private String SportCode;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeDEsc() {
        return codeDEsc;
    }

    public void setCodeDEsc(String codeDEsc) {
        this.codeDEsc = codeDEsc;
    }

    public String getSportCode() {
        return SportCode;
    }

    public void setSportCode(String sportCode) {
        SportCode = sportCode;
    }
}