package com.lesports.crawler.model.config;

import com.lesports.crawler.model.source.Content;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 输出配置
 * 
 * @author denghui
 *
 */
@Document(collection = "output_config")
@CompoundIndexes({
        @CompoundIndex(name = "site_1_content_1", def = "{'site': 1, 'content':1}", unique = true)
})
public class OutputConfig implements Serializable {
    private static final long serialVersionUID = 755518115768832009L;
    @Id
    private String id;
    // 站点
    private String site;
    // 内容
    private Content content;
    // 删除
    private boolean deleted;
    // 默认优先级，配置接口中site输出顺序
    private Short priority;
    // 输出选项
    @Field("output_option")
    private OutputOption outputOption = OutputOption.NONE;
    @Field("icon_name")
    private String iconName;
    @Field("icon_url")
    private String iconUrl;
    // 赛事列表
    private List<Long> cids = new ArrayList<>();
    // 域名
    private Set<String> domains = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public OutputOption getOutputOption() {
        return outputOption;
    }

    public void setOutputOption(OutputOption outputOption) {
        this.outputOption = outputOption;
    }

    public List<Long> getCids() {
        return cids;
    }

    public void setCids(List<Long> cids) {
        this.cids = cids;
    }

    public Set<String> getDomains() {
        return domains;
    }

    public void setDomains(Set<String> domains) {
        this.domains = domains;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Short getPriority() {
        return priority;
    }

    public void setPriority(Short priority) {
        this.priority = priority;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

}
