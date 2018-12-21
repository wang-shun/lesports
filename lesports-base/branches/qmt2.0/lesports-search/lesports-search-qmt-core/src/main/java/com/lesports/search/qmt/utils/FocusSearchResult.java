/**
 * 
 */
package com.lesports.search.qmt.utils;

import java.util.List;

import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
public class FocusSearchResult {

	private Indexable focusEntity;

	private List<Indexable> reference;

	private Long totalRef;

	/**
	 * @param focusEntity
	 * @param reference
	 * @param totalRef
	 */
	public FocusSearchResult(Indexable focusEntity, List<Indexable> reference, Long totalRef) {
		this.focusEntity = focusEntity;
		this.reference = reference;
		this.totalRef = totalRef;
	}

	public IndexType getFocusType() {
		return focusEntity != null ? focusEntity.getIndexType() : null;
	}

	public Indexable getFocus() {
		return focusEntity;
	}

	public PageResult<Indexable> getReference() {
		return new PageResult<Indexable>(reference, totalRef);
	}
}
