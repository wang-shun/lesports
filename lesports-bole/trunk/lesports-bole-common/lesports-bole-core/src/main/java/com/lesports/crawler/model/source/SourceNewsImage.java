package com.lesports.crawler.model.source;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 源新闻配图
 * 
 * @author denghui
 *
 */
public class SourceNewsImage implements Serializable {

	private static final long serialVersionUID = 143332999187561437L;
	private String url;
	private String name;
	private String desc;
	@Field("is_cover")
	private Boolean isCover;
	private Short order;
	
	public SourceNewsImage() {
		super();
	}

	public SourceNewsImage(String imageUrl) {
		super();
		this.url = imageUrl;
	}

	public SourceNewsImage(String imageUrl, String imageName) {
		super();
		this.url = imageUrl;
		this.name = imageName;
	}

	public SourceNewsImage(String imageUrl, String imageName, Short showOrder) {
        super();
        this.url = imageUrl;
        this.name = imageName;
        this.order = showOrder;
    }

    public String getImageUrl() {
		return url;
	}

	public void setImageUrl(String imageUrl) {
		this.url = imageUrl;
	}

	public String getImageName() {
		return name;
	}

	public void setImageName(String imageName) {
		this.name = imageName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Boolean getIsCover() {
		return isCover;
	}

	public void setIsCover(Boolean isCover) {
		this.isCover = isCover;
	}

	public Short getOrder() {
		return order;
	}

	public void setOrder(Short showOrder) {
		this.order = showOrder;
	}

}
