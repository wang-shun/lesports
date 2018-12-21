package com.lesports.bole.vo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.QueryParam;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/10/28
 */
public class SearchParam {

    @QueryParam("q")
    private String q;
    @QueryParam("id")
    private Long id;
    @QueryParam("name")
    private String name;
    @QueryParam("onlineStatus")
    private Integer onlineStatus;
    @QueryParam("newsType")
    private Integer newsType;
    @QueryParam("publishAt")
    private List<String> publishAt;
    @QueryParam("mids")
    private Long mids;
    @QueryParam("deleted")
    private Boolean deleted;
    @QueryParam("platform")
    private Integer platforms;
    @QueryParam("status")
    private List<Integer> status;

    public Map<String, Object> toParam(String logic, String sort) {
        Map<String, Object> param = Maps.newHashMap();
        ArrayList<Object> sortList = Lists.newArrayList();
        param.put("sort", sortList);
        Map<String, Object> updateAt = Maps.newHashMap();
        sortList.add(updateAt);
        updateAt.put(sort, "desc");

        Map<String, Object> score = Maps.newHashMap();
        sortList.add(score);
        score.put("_score", "desc");
        Map<String, Object> query = Maps.newHashMap();
        Map<String, Object> bool = Maps.newHashMap();
        query.put("bool", bool);
        ArrayList<Object> should = Lists.newArrayList();
        bool.put(logic, should);
        if (StringUtils.isNotEmpty(getQ())) {
            should.add(getQueryOfQ());
        }
        should.addAll(getQueryOfProperties());

        param.put("query", query);

        return param;
    }

    /**
     * 通过id或name获取查询条件
     *
     * @return
     */
    private Map<String, Object> getQueryOfQ() {
        Map<String, Object> query = Maps.newHashMap();
        Map<String, Object> multiMatch = Maps.newHashMap();
        multiMatch.put("query", q);
        multiMatch.put("type", "best_fields");
        multiMatch.put("lenient", true);
        multiMatch.put("fields",
                Lists.newArrayList("id", //"competitionId", "competitionSeasonId","smsId", "competitorIds",
                        "name", "competitionName", "competitionSeasonName", "competitorNames"));
        query.put("multi_match", multiMatch);
        return query;
    }

    /**
     * 构造通过多个条件的查询
     *
     * @return
     */
    private List<Object> getQueryOfProperties() {
        ArrayList<Object> should = Lists.newArrayList();
        try {
            Field[] fields = getClass().getDeclaredFields();        //获取实体类的所有属性，返回Field数组
            for (int j = 0; j < fields.length; j++) {     //遍历所有属性
                Field field = fields[j];
                String name = field.getName();    //获取属性的名字
                if (name.equals("q")) {
                    continue;
                }
                field.setAccessible(true);
                Object value = field.get(this);
                if (value != null) {
                    if (name.equals("publishAt")) {
                        Map<String, Object> publishAt = getPublishAtQuery(value);
                        if (null != publishAt) {
                            should.add(publishAt);
                        }
                    } else if (value instanceof List) {
                        if (CollectionUtils.isEmpty((List)value)) {
                            continue;
                        }
                        Map<String, Object> match = Maps.newHashMap();
                        should.add(match);
                        Map<String, Object> q = Maps.newHashMap();
                        match.put("terms", q);
                        q.put(name, value);
                    } else {
                        Map<String, Object> match = Maps.newHashMap();
                        should.add(match);
                        Map<String, Object> q = Maps.newHashMap();
                        match.put("match_phrase", q);
                        q.put(name, value);
                    }
                }
            }
        } catch (Exception e) {
        }
        return should;
    }

    /**
     * 构造新闻的publish at的查询query，value为list，第0个元素为查询时间段的开始，第1个元素为查询时间段的结束。
     * 如果list中只有一个元素，默认为查询只指定了开始，没有指定结束
     * 如果查询只希望指定结束，不希望指定开始，第0个元素需要为null
     *
     * @param value
     * @return
     */
    private Map<String, Object> getPublishAtQuery(Object value) {
        if (value instanceof List && ((List) value).size() > 0) {
            Map<String, Object> rangeP = Maps.newHashMap();
            Map<String, Object> range = Maps.newHashMap();
            Map<String, Object> publishAt = Maps.newHashMap();
            range.put("publishAt", publishAt);
            Object low = ((List) value).get(0);
            if (null != low) {
                publishAt.put("gte", low);
            }
            if (((List) value).size() > 1) {
                Object upper = ((List) value).get(1);
                if (null != upper) {
                    publishAt.put("lt", upper);
                }
            }
            rangeP.put("range", range);
            return rangeP;
        }
        return null;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public Integer getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Integer onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public Integer getNewsType() {
        return newsType;
    }

    public void setNewsType(Integer newsType) {
        this.newsType = newsType;
    }

    public List<String> getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(List<String> publishAt) {
        this.publishAt = publishAt;
    }

    public Long getMids() {
        return mids;
    }

    public void setMids(Long mids) {
        this.mids = mids;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public void setName(String name) {
        this.name = name;
    }
}
