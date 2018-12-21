package com.lesports.bole.service;

import com.lesports.api.common.LanguageCode;
import com.lesports.bole.index.EpisodeIndex;
import com.lesports.bole.index.LangString;
import com.lesports.utils.LeDateUtils;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/11
 */
public class EpisodeIndexServiceTest {
	@Resource
	private EpisodeIndexService episodeIndexService;

	@Test
	public void testSaveEpisodeIndex() {
		EpisodeIndex episodeIndex = new EpisodeIndex();
		episodeIndex.setId(19999005l);
		episodeIndex.setName("哈哈");
		List<LangString> langStrings = new ArrayList<>();

		LangString langString = new LangString();
		langString.setLanguage(LanguageCode.ZH_CN.getValue());
		langString.setValue("哈哈");
		langStrings.add(langString);

		LangString langString1 = new LangString();
		langString1.setLanguage(LanguageCode.EN_US.getValue());
		langString1.setValue("haha");
		langStrings.add(langString1);

		LangString langString2 = new LangString();
		langString2.setLanguage(LanguageCode.ZH_HK.getValue());
		langString2.setValue("囧囧");
		langStrings.add(langString2);

		episodeIndex.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));

		episodeIndexService.save(episodeIndex);
	}

	@Test
	public void testQueryEpisodeIndex() {
		EpisodeIndex episodeIndex = episodeIndexService.findOne(19999005l);
		Assert.notNull(episodeIndex);
	}

}