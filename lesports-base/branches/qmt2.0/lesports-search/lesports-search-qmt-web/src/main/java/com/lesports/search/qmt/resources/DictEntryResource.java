package com.lesports.search.qmt.resources;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.stereotype.Component;

import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.search.qmt.index.config.DictEntryIndex;
import com.lesports.search.qmt.meta.MetaData.IndexType;
import com.lesports.search.qmt.param.DictEntryIndexSearchParam;
import com.lesports.search.qmt.utils.PageResult;

/**
 * trunk.
 *
 * @author sunyue7
 * 
 */
@Component
@Path("/s/qmt")
public class DictEntryResource extends AbstractSearchResource<DictEntryIndexSearchParam> {

	@SuppressWarnings("unchecked")
	@GET
	@LJSONP
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/dict_entry/")
	public PageResult<DictEntryIndex> search(@BeanParam DictEntryIndexSearchParam param) {
		return (PageResult<DictEntryIndex>) super.search(param, IndexType.DICT_ENTRY);
	}

}
