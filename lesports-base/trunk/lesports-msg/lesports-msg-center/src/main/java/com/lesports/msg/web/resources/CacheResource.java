package com.lesports.msg.web.resources;

import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;
import com.lesports.msg.cache.*;
import com.lesports.msg.model.CacheCleanResult;
import com.lesports.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.net.URI;
import java.net.URLDecoder;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/23
 */
@Path("/caches")
@Component
public class CacheResource {
    private static final Logger LOG = LoggerFactory.getLogger(CacheResource.class);

    @Inject
    private NgxCmsMemCache ngxCmsMemCache;
    @Inject
    private NgxCmsMemCacheTDXY ngxCmsMemCacheTDXY;
    @Inject
    private NgxApiMemCache ngxApiMemCache;

    @Inject
    private NgxApiMemCacheTDXY ngxApiMemCacheTDXY;

    @DELETE
    @Produces({AlternateMediaType.UTF_8_APPLICATION_JSON})
    public CacheCleanResult clean(@QueryParam("url") String url,
                                  @QueryParam("cdn") @DefaultValue("true") boolean cdn,
                                  @QueryParam("nginx") @DefaultValue("true") boolean nginx) {
        try {
            CacheCleanResult result = new CacheCleanResult();
            if (StringUtils.isNotEmpty(url)) {
                String realUrl = URLDecoder.decode(url, "UTF-8");
                URI uri = new URI(realUrl);
                if (null != uri) {
                    String host = uri.getHost();
                    String path = uri.getPath();
                    String query = uri.getQuery();
                    if ("api.lesports.com".equalsIgnoreCase(host) ||
                            "api.sports.letv.com".equalsIgnoreCase(host) ||
                            "p.api.sports.letv.com".equalsIgnoreCase(host) ||
                            "p.api.lesports.com".equalsIgnoreCase(host) ||
                            "u.api.lesports.com".equalsIgnoreCase(host) ||
                            "u.api.sports.letv.com".equalsIgnoreCase(host) ||
                            "static.api.sports.letv.com".equalsIgnoreCase(host) ||
                            "static.p.api.sports.letv.com".equalsIgnoreCase(host) ||
                            "static.u.api.sports.letv.com".equalsIgnoreCase(host) ||
                            "internal.api.lesports.com".equalsIgnoreCase(host)) {
                        String key4Mem = getMemKeyForApi(path, query);
                        if (nginx) {
                            ngxApiMemCache.delete(key4Mem, -1);
                            ngxApiMemCacheTDXY.delete(key4Mem, -1);
                            HkCacheApis.deleteNgx(key4Mem, -1L);
                        }
                        if (cdn) {
                            CdnCacheApis.delete(url);
                        }
                    } else if ("op.api.lesports.com".equalsIgnoreCase(host)) {
                        String key4Mem = getMD5KeyForOp(path, query);
                        if (nginx) {
                            ngxApiMemCache.delete(key4Mem, -1);
                            ngxApiMemCacheTDXY.delete(key4Mem, -1);
                            HkCacheApis.deleteNgx(key4Mem, -1L);
                        }
                        if (cdn) {
                            CdnCacheApis.delete(url);
                        }
                    } else {
                        if (nginx) {
                            ngxCmsMemCache.delete(url, -1);
                            ngxCmsMemCacheTDXY.delete(url, -1);
                            HkCacheApis.deleteNgx(url, -1l);
                        }
                        if (cdn) {
                            CdnCacheApis.delete(url);
                        }
                    }
                }

            }
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * filter parameters according to http://svn2.letv.cn/tp/lesports/devops/nginx/api/conf/conf.d/sr_cache.conf
     * local questionmark = "?"
     * local temp = ngx.arg[2]
     * temp = ngx.re.gsub(temp,"callback=.*?(&|$)","")
     * <p/>
     * temp = ngx.re.gsub(temp,"_=.*?(&|$)","")
     * temp = ngx.re.gsub(temp,"device_id=.*?(&|$)","")
     * temp = ngx.re.gsub(temp,"device_model=.*?(&|$)","")
     * temp = ngx.re.gsub(temp,"system_name=.*?(&|$)","")
     * temp = ngx.re.gsub(temp,"system_version=.*?(&|$)","")
     * temp = ngx.re.gsub(temp,"build_model=.*?(&|$)","")
     * temp = ngx.re.gsub(temp,"build_version=.*?(&|$)","")
     * temp = ngx.re.gsub(temp,"nowTime=.*?(&|$)","")
     * temp = ngx.re.gsub(temp,"localized_model=.*?(&|$)","")
     * <p/>
     * temp = ngx.re.gsub(temp,"_method=.*?(&|$)","")
     * temp = ngx.re.gsub(temp, "&$", "")
     * temp = ngx.re.gsub(temp, "^&", "")
     * temp = ngx.re.gsub(temp, "&&", "&")
     * if nil == temp or "" == temp then
     * questionmark = ""
     * end
     * temp = ngx.arg[1]..questionmark..temp
     * -- MD5 the key
     * return ngx.md5(temp)
     *
     * @param query
     * @return
     */
    private String getMemKeyForApi(String path, String query) {
        String questionMark = "?";
        String temp = query.replaceAll("callback=.*?(&|$)", "");
        temp = temp.replaceAll("_=.*?(&|$)", "");
        temp = temp.replaceAll("device_id=.*?(&|$)", "");
        temp = temp.replaceAll("device_model=.*?(&|$)", "");
        temp = temp.replaceAll("system_name=.*?(&|$)", "");
        temp = temp.replaceAll("system_version=.*?(&|$)", "");
        temp = temp.replaceAll("build_model=.*?(&|$)", "");
        temp = temp.replaceAll("build_version=.*?(&|$)", "");
        temp = temp.replaceAll("nowTime=.*?(&|$)", "");
        temp = temp.replaceAll("localized_model=.*?(&|$)", "");
        temp = temp.replaceAll("_method=.*?(&|$)", "");
        temp = temp.replaceAll("&$", "");
        temp = temp.replaceAll("^&", "");
        temp = temp.replaceAll("&&", "");
        temp = temp.replaceAll("callback=.*?(&|$)", "");
        if (StringUtils.isEmpty(temp)) {
            questionMark = "";
        }
        temp = path + questionMark + temp;
        return MD5Util.md5(temp);
    }

    /**
     * get key for op http://svn2.letv.cn/tp/lesports/devops/nginx/api/conf/conf.d/op.api.lesports.com.conf
     * set_by_lua $mem_key '
     * local questionmark = "?"
     * local temp = ngx.arg[1]..questionmark..ngx.arg[2]
     * --md5 the key
     * return ngx.md5(temp)
     * ' $uri $args;
     *
     * @param path
     * @param query
     * @return
     */
    private String getMD5KeyForOp(String path, String query) {
        String questionMark = "?";
        String temp = path + questionMark + query;
        return MD5Util.md5(temp);
    }
}
