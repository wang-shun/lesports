package com.lesports.crawler.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/18
 */
@Document(collection = "seed_requests")
public class SeedRequest {
    @Id
    private String id;
    private Boolean effect;
    private Integer frequency;
    
    public Boolean getEffect() {
        return effect;
    }

    public void setEffect(Boolean effect) {
        this.effect = effect;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }
    
}
