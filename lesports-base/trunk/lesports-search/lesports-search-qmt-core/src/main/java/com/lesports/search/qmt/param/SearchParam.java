/**
 * 
 */
package com.lesports.search.qmt.param;

import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

/**
 * @author sunyue7
 *
 */
public interface SearchParam {

	public NativeSearchQueryBuilder createNativeSearchQueryBuilder();
	
	public void validate();
	
}
