package com.lesports.crawler.pipeline.matcher;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class Zhibo8MatcherTest {

  @SuppressWarnings("serial")
  private static Set<String>  redundantStrings = new HashSet<String>(){
    {
      add("季前赛");
      add("常规赛");
      add("季后赛");
      add("分区决赛");
      add("总决赛");
      
      add("小组赛");
      add("淘汰赛");
      add("1/8决赛");
      add("1/4决赛");
      add("半决赛");
      add("决赛");
      
      add("第[0-9]*轮");
      add("[A-Z]组");
    }
  };
  
  String matchCompetition(String value) {
    String competition = value;
    // 过滤特定词
    for (String string : redundantStrings) {
      competition = competition.replaceFirst(string, "");
    }
    
    return competition;
  }
  
  @Test
  public void testMatchCompetition() {
    Assert.assertEquals("NBA", matchCompetition("NBA常规赛"));
    Assert.assertEquals("英超", matchCompetition("英超第10轮"));
    Assert.assertEquals("欧冠", matchCompetition("欧冠小组赛H组"));
  }
}
