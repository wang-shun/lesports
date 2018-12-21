package com.lesports.crawler.pipeline.matcher;

/**
 * 匹配结果
 * 
 * @author denghui
 *
 * @param <T>
 */
public class MatcherResult<T> {

	private String preprocess;
	private T matched;

	public MatcherResult() {
		super();
	}

	public MatcherResult(String preprocess, T matched) {
		super();
		this.preprocess = preprocess;
		this.matched = matched;
	}

	public String getPreprocess() {
		return preprocess;
	}

	public void setPreprocess(String preprocess) {
		this.preprocess = preprocess;
	}

	public T getMatched() {
		return matched;
	}

	public void setMatched(T matched) {
		this.matched = matched;
	}

}
