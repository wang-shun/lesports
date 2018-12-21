package com.lesports.crawler.model.source;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/18
 */
@Document(collection = "source_matches")
@CompoundIndexes({ @CompoundIndex(name = "source_1_source_id_1", def = "{'source': 1, 'source_id': 1}", unique = true) })
public class SourceMatch implements Serializable {
    private static final long serialVersionUID = 8469974207065550288L;
    @Id
    private String id;
    // 比赛名称
    private String name;
    // 来源
    private Source source;
    // 来源ID
    @Field("source_id")
    private String sourceId;
    // 比赛开始时间
    @Field("start_time")
    @Indexed(name = "start_time_-1", direction = IndexDirection.DESCENDING)
    private String startTime;
    // 赛事名称
    private String competition;
    // 是否对阵，默认是
    private Boolean vs = Boolean.TRUE;
    // 对阵方名称
    private List<String> competitors;
    // 大项
    @Field("game_f_type")
    private Long gameFType;
    // 大项名称
    @Field("game_f_name")
    private String gameFName;
    // 轮次
    private Integer round;
    // 直播信息
    private List<SourceLive> lives = Lists.newArrayList();
    // 来源页面URL
    @Field("source_url")
    private String sourceUrl;
    @Field("source_data")
    private Object sourceData;
    // 处理结果
    private SourceResult result;
    // 创建时间
    @Field("create_at")
    private String createAt;
    // 更新时间
    @Field("update_at")
    private String updateAt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public List<SourceLive> getLives() {
        return lives;
    }

    public void setLives(List<SourceLive> lives) {
        this.lives = lives;
    }

    public void addLive(SourceLive live) {
        if (live != null) {
            this.lives.add(live);
        }
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public List<String> getCompetitors() {
        return competitors;
    }

    public void setCompetitors(List<String> competitors) {
        this.competitors = competitors;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public SourceResult getResult() {
        return result;
    }

    public void setResult(SourceResult result) {
        this.result = result;
    }

    public Long getGameFType() {
        return gameFType;
    }

    public void setGameFType(Long gameFType) {
        this.gameFType = gameFType;
    }

    public Object getSourceData() {
        return sourceData;
    }

    public void setSourceData(Object sourceData) {
        this.sourceData = sourceData;
    }

    public Boolean getVs() {
        return vs;
    }

    public void setVs(Boolean vs) {
        this.vs = vs;
    }

    public String getGameFName() {
        return gameFName;
    }

    public void setGameFName(String gameFName) {
        this.gameFName = gameFName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SourceMatch [id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append(", source=");
        builder.append(source);
        builder.append(", sourceId=");
        builder.append(sourceId);
        builder.append(", startTime=");
        builder.append(startTime);
        builder.append(", competition=");
        builder.append(competition);
        builder.append(", vs=");
        builder.append(vs);
        builder.append(", competitors=");
        builder.append(competitors);
        builder.append(", gameFType=");
        builder.append(gameFType);
        builder.append(", gameFName=");
        builder.append(gameFName);
        builder.append(", round=");
        builder.append(round);
        builder.append(", lives=");
        builder.append(lives);
        builder.append(", sourceUrl=");
        builder.append(sourceUrl);
        builder.append(", sourceData=");
        builder.append(sourceData);
        builder.append(", result=");
        builder.append(result);
        builder.append(", createAt=");
        builder.append(createAt);
        builder.append(", updateAt=");
        builder.append(updateAt);
        builder.append("]");
        return builder.toString();
    }

}
