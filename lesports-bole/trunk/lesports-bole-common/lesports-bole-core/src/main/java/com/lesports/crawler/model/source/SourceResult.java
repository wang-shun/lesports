package com.lesports.crawler.model.source;

/**
 * Source处理结果
 * 
 * @author denghui
 *
 */
public class SourceResult {
    public static final Integer STAGE_COMPETITION = 1;
    public static final Integer STAGE_COMPETITION_SEASON = 2;
    public static final Integer STAGE_COMPETITOR = 3;
    public static final Integer STAGE_MATCH = 4;
    
    // 执行到的阶段
    private Integer stage;
    // 是否匹配到Bole对象
    private Boolean matched;
    // 匹配到的或者未匹配到而新建的Bole对象Id
    private Long boleId;

    public SourceResult() {
        super();
    }

    public SourceResult(Integer stage, Boolean matched, Long boleId) {
        super();
        this.stage = stage;
        this.matched = matched;
        this.boleId = boleId;
    }

    public Integer getStage() {
        return stage;
    }

    public void setStage(Integer stage) {
        this.stage = stage;
    }

    public Boolean getMatched() {
        return matched;
    }

    public void setMatched(Boolean matched) {
        this.matched = matched;
    }

    public Long getBoleId() {
        return boleId;
    }

    public void setBoleId(Long boleId) {
        this.boleId = boleId;
    }

}
