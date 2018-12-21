package com.lesports.search.qmt.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/10/28
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticSearchResult {
    private Integer took;
    @JsonProperty("time_out")
    private Boolean timeOut;
    @JsonProperty("hits")
    private Hits hits;

    public Integer getTook() {
        return took;
    }

    public void setTook(Integer took) {
        this.took = took;
    }

    public Boolean getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Boolean timeOut) {
        this.timeOut = timeOut;
    }

    public Hits getHits() {
        return hits;
    }

    public void setHits(Hits hits) {
        this.hits = hits;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hits {
        private Integer total;
        @JsonProperty("max_score")
        private Double maxScore;
        @JsonProperty("hits")
        private List<HitItem> hits;

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Double getMaxScore() {
            return maxScore;
        }

        public List<HitItem> getHits() {
            return hits;
        }

        public void setHits(List<HitItem> hits) {
            this.hits = hits;
        }

        public void setMaxScore(Double maxScore) {
            this.maxScore = maxScore;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HitItem {
        @JsonProperty("_index")
        private String index;
        @JsonProperty("_type")
        private String type;
        @JsonProperty("_id")
        private String id;
        @JsonProperty("_score")
        private Double score;
        @JsonProperty("_source")
        private Map<String, Object> source;

        public Map<String, Object> getSource() {
            return source;
        }

        public void setSource(Map<String, Object> source) {
            this.source = source;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Double getScore() {
            return score;
        }

        public void setScore(Double score) {
            this.score = score;
        }
    }

}
