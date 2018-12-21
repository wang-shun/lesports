package com.lesports.bole.service.impl;

import com.google.common.collect.Lists;
import com.lesports.bole.index.VideoIndex;
import com.lesports.bole.repository.CommonSearchRepository;
import com.lesports.bole.repository.VideoIndexRepository;
import com.lesports.bole.service.AbstractSearchService;
import com.lesports.bole.service.VideoIndexService;
import com.lesports.bole.utils.PageResult;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.model.Video;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/11
 */
@Service
public class VideoIndexServiceImpl extends AbstractSearchService<VideoIndex, Long> implements VideoIndexService {
    @Resource
    private VideoIndexRepository videoIndexRepository;
    @Resource
    private CommonSearchRepository commonSearchRepository;

    @Override
    public PageResult<VideoIndex> findByParams(long id, String name, Integer type, Integer countryCode, Integer platform, Integer supportLicence, List<String> updateAt, int page, int count) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(new TermQueryBuilder("deleted", false));
        if (id > 0){
            boolQueryBuilder.must(new TermQueryBuilder("id", id));
        }
        if (StringUtils.isNotEmpty(name)) {
            boolQueryBuilder.must(new NestedQueryBuilder("multiLangNames", new MatchQueryBuilder("multiLangNames.value", name).type(MatchQueryBuilder.Type.PHRASE)));
        }
        if (null != type) {
            boolQueryBuilder.must(new TermQueryBuilder("type", type));
        }
        if (null != platform) {
            boolQueryBuilder.must(new TermQueryBuilder("platforms", platform));
        }
        if (null != countryCode) {
            boolQueryBuilder.must(new TermQueryBuilder("allowCountries", countryCode));
        }
        if (null != supportLicence) {
            boolQueryBuilder.must(new TermQueryBuilder("supportLicences", supportLicence));
        }
        if (CollectionUtils.isNotEmpty(updateAt)) {
            if (updateAt.size() == 2) {
                if (null != updateAt.get(0)) {
                    boolQueryBuilder.must(new RangeQueryBuilder("updateAt").gte(updateAt.get(0)).lt(updateAt.get(1)));
                } else {
                    boolQueryBuilder.must(new RangeQueryBuilder("updateAt").lt(updateAt.get(1)));
                }
            } else {
                boolQueryBuilder.must(new RangeQueryBuilder("updateAt").gte(updateAt.get(0)));
            }
        }
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withPageable(new PageRequest(page, count, new Sort(Sort.Direction.DESC, "updateAt")));

        Page<VideoIndex> res = commonSearchRepository.findBySearchQuery(nativeSearchQueryBuilder.build(), VideoIndex.class);
        return new PageResult<>(res);
    }

    @Override
    protected VideoIndex getEntityFromRpc(Long idLong) {
        Video tVideo = SopsInternalApis.getVideoById(idLong);
        if (null == tVideo) {
            return null;
        }
        VideoIndex videoIndex = new VideoIndex();
        videoIndex.setId(idLong);
        videoIndex.setDeleted(tVideo.getDeleted());
        videoIndex.setUpdateAt(tVideo.getUpdateAt());
        videoIndex.setName(tVideo.getName());
        videoIndex.setType(tVideo.getType().getValue());
        if (CollectionUtils.isNotEmpty(tVideo.getSupportLicences())){
            videoIndex.setSupportLicences(Lists.transform(Lists.newArrayList(tVideo.getSupportLicences()), TV_LICENCE_FUNCTION));
        }
        if (CollectionUtils.isNotEmpty(tVideo.getMultiLangNames())) {
            videoIndex.setMultiLangNames(Lists.transform(tVideo.getMultiLangNames(), LANG_STRING_FUNCTION));
        }
        if (CollectionUtils.isNotEmpty(tVideo.getAllowCountries())) {
            videoIndex.setAllowCountries(Lists.transform(tVideo.getAllowCountries(), COUNTRY_CODE_FUNCTION));
        }
        if (CollectionUtils.isNotEmpty(tVideo.getPlatforms())) {
            videoIndex.setPlatforms(Lists.transform(Lists.newArrayList(tVideo.getPlatforms()), PLATFORM_FUNCTION));
        }
        return videoIndex;
    }

    @Override
    public boolean save(VideoIndex entity) {
        videoIndexRepository.save(entity);
        return true;
    }

    @Override
    protected VideoIndex doFindOne(Long aLong) {
        return videoIndexRepository.findOne(aLong);
    }
}
