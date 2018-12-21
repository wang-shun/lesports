package com.lesports.bole.resources;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.lesports.bole.api.ElasticApis;
import com.lesports.bole.function.SearchIndexTransformer;
import com.lesports.bole.index.NewsIndex;
import com.lesports.bole.index.SearchIndex;
import com.lesports.bole.service.BoleSearchCrudService;
import com.lesports.bole.service.BoleSearchServiceFactory;
import com.lesports.bole.service.CommonSearchService;
import com.lesports.bole.service.IndexDbService;
import com.lesports.bole.utils.PageResult;
import com.lesports.bole.vo.SearchParam;
import com.lesports.bole.vo.SuccessVo;
import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/9/18
 */
@Path("/")
public class SearchResource {

	private static final Logger LOG = LoggerFactory.getLogger(SearchResource.class);

	@Inject
	private BoleSearchServiceFactory boleSearchServiceFactory;

	@Inject
	private CommonSearchService commonSearchService;

	@Inject
	private IndexDbService indexDbService;

	/**
	 * @return
	 */
	@GET
	@LJSONP
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/s/{index}/{type}/")
	public PageResult<SearchIndex> search(@PathParam("index") String index, @PathParam("type") String type,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("count") @DefaultValue("10") int count,
			@QueryParam("logic") @DefaultValue("must") String logic, @BeanParam SearchParam searchParam) {
		PageResult<SearchIndex> result;
		if (null == searchParam) {
			return new PageResult<>();
		}
		try {
			String sortBy = "updateAt";
			Class clazz = SearchIndexTransformer.getClass(index, type);
			if (clazz == NewsIndex.class) {
				sortBy = "publishAt";
				if (null == searchParam.getDeleted()) {
					searchParam.setDeleted(false);
				}
			}
			Map<String, Object> param = searchParam.toParam(logic, sortBy);
			result = ElasticApis.search(index, type, param, page, count, true);
		} catch (Exception e) {
			LOG.error("fail to execute search index : {}, type : {}, search param : {}. error : {}", index, type,
					JSONObject.toJSONString(searchParam), e.getMessage(), e);
			throw new LeWebApplicationException("", LeStatus.EXPECTATION_FAILED);
		}
		return result;
	}

	/**
	 * @return
	 */
	@PUT
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/i/{index}/{type}/{id}")
	public SuccessVo index(@PathParam("index") String index, @PathParam("type") String type, @PathParam("id") Long id) {
		SuccessVo successVo = new SuccessVo();
		try {
			Class clazz = SearchIndexTransformer.getClass(index, type);
			BoleSearchCrudService service = boleSearchServiceFactory.getService(clazz);
			if (null != service) {
				successVo.setSuccess(service.save(id));
			} else {
				successVo.setSuccess(false);
			}
		} catch (Exception e) {
			successVo.setSuccess(false);
			LOG.error("fail to index object index : {}, type : {}, id : {}. error : {}", index, type, id,
					e.getMessage(), e);
			throw new LeWebApplicationException("", LeStatus.EXPECTATION_FAILED);
		}
		return successVo;
	}

	/**
	 * @return
	 */
	@POST
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/i/bulk/{index}/{type}/{ids}")
	public SuccessVo indexBulk(@PathParam("index") String index, @PathParam("type") String type,
			@PathParam("ids") String ids) {
		SuccessVo successVo = new SuccessVo();
		try {
			Class clazz = SearchIndexTransformer.getClass(index, type);
			BoleSearchCrudService service = boleSearchServiceFactory.getService(clazz);
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
				LOG.info("bulk index:{},type:{},data:{}", index, type, list);
				successVo.setSuccess(commonSearchService.saveBulk(list));
			} else {
				successVo.setSuccess(false);
			}
		} catch (Exception e) {
			successVo.setSuccess(false);
			LOG.error("fail to index object index : {}, type : {}, ids : {}. error : {}", index, type, ids,
					e.getMessage(), e);
			throw new LeWebApplicationException("", LeStatus.EXPECTATION_FAILED);
		}
		return successVo;
	}

	@POST
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/i/bulkdb/{index}/{type}/{table}")
	public SuccessVo indexDbBulk(@PathParam("index") String index, @PathParam("type") String type,
			@PathParam("table") String table) {
		SuccessVo successVo = new SuccessVo();
		try {
			successVo.setSuccess(indexDbService.indexFromDb(index, type, table));
		} catch (Exception e) {
			successVo.setSuccess(false);
			LOG.error("fail to index from MongoDB, index : {}, type : {}, table : {}. error : {}", index, type, table,
					e.getMessage(), e);
			throw new LeWebApplicationException("", LeStatus.EXPECTATION_FAILED);
		}
		return successVo;
	}
}
