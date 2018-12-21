/**
 * 
 */
package com.lesports.search.qmt.resources;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.lesports.id.api.IdType;
import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;
import com.lesports.search.qmt.param.IndexMongoParam;
import com.lesports.search.qmt.service.CommonSearchService;
import com.lesports.search.qmt.service.SearchCrudService;
import com.lesports.search.qmt.utils.SearchServiceFactory;
import com.lesports.search.qmt.vo.SuccessVo;

/**
 * @author sunyue7
 *
 */
@Component
@Path("/i/qmt")
public class IndexerResource {

	private static final Logger LOG = LoggerFactory.getLogger(IndexerResource.class);

	@Inject
	private SearchServiceFactory searchServiceFactory;

	@Inject
	private CommonSearchService commonSearchService;

	@POST
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/{indextype}/{id}")
	public SuccessVo indexByPost(@PathParam("indextype") String type, @PathParam("id") Long id) {
		return indexByPut(type, id);
	}

	@PUT
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/{indextype}/{id}")
	public SuccessVo indexByPut(@PathParam("indextype") String type, @PathParam("id") Long id) {
		SuccessVo successVo = new SuccessVo();
		if (StringUtils.isEmpty(type) || id == null) {
			successVo.setSuccess(false);
			return successVo;
		}
		IdType idType = IdType.valueOf(type.toUpperCase());
		IndexType indexType = MetaData.IndexType.valueOf(idType);
		try {
			SearchCrudService<? extends Indexable, Long> service = searchServiceFactory.getService(indexType);
			if (null != service) {
				successVo.setSuccess(service.save(id));
			} else {
				successVo.setSuccess(false);
				LOG.error("No index service found, type : {}", type);
			}
		} catch (Exception e) {
			successVo.setSuccess(false);
			LOG.error("fail to index object index : {}, type : {}, id : {}. error : {}",
					indexType != null ? indexType.getIdType() : "", indexType != null ? indexType : type, id,
					e.getMessage(), e);
			throw new LeWebApplicationException("Index failed!", LeStatus.EXPECTATION_FAILED);
		}
		return successVo;
	}

	@POST
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/bulk/{indextype}/{ids}")
	public SuccessVo indexBulk(@PathParam("indextype") String type, @PathParam("ids") String ids) {
		SuccessVo successVo = new SuccessVo();
		if (StringUtils.isEmpty(type) || StringUtils.isEmpty(ids)) {
			successVo.setSuccess(false);
			return successVo;
		}
		IdType idType = IdType.valueOf(type.toUpperCase());
		IndexType indexType = MetaData.IndexType.valueOf(idType);
		try {
			SearchCrudService<? extends Indexable, Long> service = searchServiceFactory.getService(indexType);
			if (null != service) {
				if (StringUtils.isBlank(ids)) {
					return null;
				}
				List<Long> idArrays = Lists.newArrayList(
						Iterables.transform(Lists.newArrayList(ids.split(",")), new Function<String, Long>() {
							@Nullable
							@Override
							public Long apply(@Nullable String s) {
								return Long.parseLong(s);
							}
						}));
				List<IndexQuery> list = service.getBulkData(idArrays);
				LOG.info("bulk index:{},type:{},data:{}", indexType != null ? indexType.getIdType() : "",
						indexType != null ? indexType : type, list);
				successVo.setSuccess(commonSearchService.saveBulk(list));
			} else {
				successVo.setSuccess(false);
			}
		} catch (Exception e) {
			successVo.setSuccess(false);
			LOG.error("fail to index object index : {}, type : {}, ids : {}. error : {}",
					indexType != null ? indexType.getIdType() : "", indexType != null ? indexType : type, ids,
					e.getMessage(), e);
			throw new LeWebApplicationException("Index bulk failed!", LeStatus.EXPECTATION_FAILED);
		}
		return successVo;
	}

	@POST
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/mongo/{indextype}/{db}/{table}")
	public SuccessVo indexBulkFromMongo(@PathParam("indextype") String type, @PathParam("db") String db,
			@PathParam("table") String table, @BeanParam IndexMongoParam param) {
		SuccessVo successVo = new SuccessVo();
		if (StringUtils.isEmpty(type) || StringUtils.isEmpty(table)) {
			successVo.setSuccess(false);
			return successVo;
		}
		IdType idType = IdType.valueOf(type.toUpperCase());
		IndexType indexType = MetaData.IndexType.valueOf(idType);
		try {
			SearchCrudService<? extends Indexable, Long> service = searchServiceFactory.getService(indexType);
			if (null != service) {
				if (StringUtils.isBlank(table)) {
					return null;
				}
				Long count = service.createIndexFromMongo(db, table, param);
				successVo.setMessage(
						count + " entities are being indexed from " + (param.isRpc() ? "RPC..." : "Mongodb..."));
				successVo.setSuccess(true);
			} else {
				successVo.setSuccess(false);
			}
		} catch (Exception e) {
			successVo.setSuccess(false);
			LOG.error("fail to index, index : {}, type : {}, db : {}, table : {}. error : {}",
					indexType != null ? indexType.getIdType() : "", indexType != null ? indexType : type, db, table,
					e.getMessage(), e);
			throw new LeWebApplicationException("Index bulk from Mongo failed!", LeStatus.EXPECTATION_FAILED);
		}
		return successVo;
	}
}
