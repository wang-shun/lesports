package com.lesports.bole.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.lesports.crawler.model.source.Source;

/**
 * Bole赛事
 *
 * @author denghui
 */
@Document(collection = "bole_competitions")
public class BoleCompetition implements Serializable {

    private static final long serialVersionUID = 15391575487920063L;
    @Id
    private Long id;
    // 赛事名称
    @Indexed(name = "name_1")
    private String name;
    // 赛事名称缩写
    @Indexed(name = "abbreviation_1")
    private String abbreviation;
    // 大项
    @Field("game_f_type")
    private Long gameFType;
    // 是否为对阵，默认是
    private Boolean vs = Boolean.TRUE;
    // Bole状态
    private BoleStatus status;
    // 标识新建赛事的来源
    private Source source;
    @Field("source_match_id")
    private String sourceMatchId;
    // 赛事在SMS的ID
    @Field("sms_id")
    @Indexed(name = "sms_id_1")
    private Long smsId = Long.valueOf(0);
    @Field("attach_id")
    private Long attachId = Long.valueOf(0);
    @Field("mapping_id")
    private Long mappingId = Long.valueOf(0);
    // 输出该赛事直播信息时site的排序规则
    @Field("site_order")
    private List<String> siteOrder;
    // 创建时间
    @Field("create_at")
    private String createAt;
    // 更新时间
    @Field("update_at")
    private String updateAt;
    @Field("files")
    private List<DataImportFile> files;

    public static class DataImportFile {
        @Field("file_type")
        private String fileType;
        @Field("file_name")
        private String fileName;
        @Field("file_partner_type")
        private String partner_type;
        @Field("create_at")
        private String createAt;
        // 更新时间
        @Field("update_at")
        private String updateAt;
        @Field("is_online")
        private Boolean isOnline;

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getUpdateAt() {
            return updateAt;
        }

        public void setUpdateAt(String updateAt) {
            this.updateAt = updateAt;
        }

        public String getCreateAt() {
            return createAt;
        }

        public void setCreateAt(String createAt) {
            this.createAt = createAt;
        }

        public String getPartner_type() {
            return partner_type;
        }

        public void setPartner_type(String partner_type) {
            this.partner_type = partner_type;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Boolean getIsOnline() {
            return isOnline;
        }

        public void setIsOnline(Boolean isOnline) {
            this.isOnline = isOnline;
        }
    }

    public List<DataImportFile> getFiles() {
        return files;
    }

    public void setFiles(List<DataImportFile> files) {
        this.files = files;
    }

    public Long getAttachId() {
        return attachId;
    }

    public void setAttachId(Long attachId) {
        this.attachId = attachId;
    }

    public Long getMappingId() {
        return mappingId;
    }

    public void setMappingId(Long mappingId) {
        this.mappingId = mappingId;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public BoleStatus getStatus() {
        return status;
    }

    public void setStatus(BoleStatus status) {
        this.status = status;
    }

    public Long getSmsId() {
        return smsId;
    }

    public void setSmsId(Long smsId) {
        this.smsId = smsId;
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

    public Long getGameFType() {
        return gameFType;
    }

    public void setGameFType(Long gameFType) {
        this.gameFType = gameFType;
    }

    public String getSourceMatchId() {
        return sourceMatchId;
    }

    public void setSourceMatchId(String sourceMatchId) {
        this.sourceMatchId = sourceMatchId;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Boolean getVs() {
        return vs;
    }

    public void setVs(Boolean vs) {
        this.vs = vs;
    }

    public List<String> getSiteOrder() {
        return siteOrder;
    }

    public void setSiteOrder(List<String> siteOrder) {
        this.siteOrder = siteOrder;
    }

}
