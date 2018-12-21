/**
 * 
 */
package com.lesports.search.qmt.index;

import com.lesports.search.qmt.meta.MetaData;

/**
 * @author sunyue7
 *
 */
public interface Indexable {

	public Boolean getDeleted();

	public void setDeleted(Boolean deleted);

	public void setUpdateAt(String updateAt);
	
	public String getUpdateAt();
	
	public MetaData.IndexType getIndexType();
	
	public Long getId();

}
