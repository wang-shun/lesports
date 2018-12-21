package com.lesports.crawler.pipeline.matcher;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * 直播吧Matcher
 * 
 * @author denghui
 *
 */
@Component("ZHIBO8Matcher")
public class Zhibo8Matcher extends DefaultMatcher {
	@SuppressWarnings("serial")
	private static List<String> redundantStrings = new ArrayList<String>() {
		{
			add("季前赛");
			add("常规赛");
			add("季后赛");
			add("总决赛");

			add("小组赛");
			add("1/8决赛");
			add("1/4决赛");
			add("五六名决赛");
			add("三四名决赛");
			add("半决赛");
			add("决赛");

			add("第[0-9]+轮");
			add("[A-Z]组");
			add("小组赛[A-Z]组");
		}
	};

	/**
	 * 过滤特定词
	 */
	protected String preprocessCompetition(String value) {
		String competition = null;
		for (String string : redundantStrings) {
			competition = value.replaceFirst(string, "");
			if (!competition.equals(value)) {
				return competition;
			}
		}
		return value;
	}
}
