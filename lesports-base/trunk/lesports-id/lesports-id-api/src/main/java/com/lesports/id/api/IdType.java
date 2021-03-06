/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.lesports.id.api;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

/**
 * Lesports User Service Definition.
 * @author ellios
 */
public enum IdType implements org.apache.thrift.TEnum {
  DICT_ENTRY(0),
  COMPETITION(1),
  COMPETITION_SEASON(2),
  MATCH(3),
  ALBUM(4),
  EPISODE(5),
  TEAM(6),
  PLAYER(7),
  TAG(8),
  MATCH_ACTION(9),
  COMPETITOR_SEASON_STAT(10),
  RECOMMEND_NEWS(11),
  TOP_LIST(12),
  TEAM_SEASON(14),
  ACTIVITY(16),
  DATA_IMPORT_CONFIG(17),
  MENU(18),
  NEWS(19),
  NEWS_IMAGE(20),
  UPGRADE(21),
  USER_ENTRY(22),
  COMMENT(23),
  BOLE_MATCH(24),
  BOLE_COMPETITION(25),
  LEVEL(26),
  PRIVILEGE(27),
  BOLE_COMPETITION_SEASON(28),
  STRATEGY(29),
  NOTICE(30),
  QUESTION(31),
  TEXT_LIVE(32),
  LIVE_MESSAGE(33),
  TEXT_LIVE_IMAGE(34),
  VOTE(35),
  ACTION_LOG(36),
  SUGGEST(37),
  VOTE_OPTION(38),
  TV_DESKTOP(39),
  MENU_ITEM(40),
  BOLE_COMPETITOR(41),
  BOLE_LIVE(42),
  RECOMMEND_TV_NEWS(43),
  BOLE_NEWS(44),
  SMS_MENU(45),
  MEDAL(46),
  RECORD(47),
  CAROUSEL(48),
  RECOMMEND_EPISODE(49),
  MEDAL_LIST(50),
  PLAYER_CAREER_STAT(51),
  BUSINESS(101),
  PRODUCT(102),
  ACTION(103),
  MEMBER_ORDER(104),
  MMS_ALBUM(1001),
  MMS_VIDEO(1002),
  LETV_LIVE(1003),
  TLIVE(1004),
  USER_SUBSCRIBE(1005);

  private final int value;

  private IdType(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static IdType findByValue(int value) { 
    switch (value) {
      case 0:
        return DICT_ENTRY;
      case 1:
        return COMPETITION;
      case 2:
        return COMPETITION_SEASON;
      case 3:
        return MATCH;
      case 4:
        return ALBUM;
      case 5:
        return EPISODE;
      case 6:
        return TEAM;
      case 7:
        return PLAYER;
      case 8:
        return TAG;
      case 9:
        return MATCH_ACTION;
      case 10:
        return COMPETITOR_SEASON_STAT;
      case 11:
        return RECOMMEND_NEWS;
      case 12:
        return TOP_LIST;
      case 14:
        return TEAM_SEASON;
      case 16:
        return ACTIVITY;
      case 17:
        return DATA_IMPORT_CONFIG;
      case 18:
        return MENU;
      case 19:
        return NEWS;
      case 20:
        return NEWS_IMAGE;
      case 21:
        return UPGRADE;
      case 22:
        return USER_ENTRY;
      case 23:
        return COMMENT;
      case 24:
        return BOLE_MATCH;
      case 25:
        return BOLE_COMPETITION;
      case 26:
        return LEVEL;
      case 27:
        return PRIVILEGE;
      case 28:
        return BOLE_COMPETITION_SEASON;
      case 29:
        return STRATEGY;
      case 30:
        return NOTICE;
      case 31:
        return QUESTION;
      case 32:
        return TEXT_LIVE;
      case 33:
        return LIVE_MESSAGE;
      case 34:
        return TEXT_LIVE_IMAGE;
      case 35:
        return VOTE;
      case 36:
        return ACTION_LOG;
      case 37:
        return SUGGEST;
      case 38:
        return VOTE_OPTION;
      case 39:
        return TV_DESKTOP;
      case 40:
        return MENU_ITEM;
      case 41:
        return BOLE_COMPETITOR;
      case 42:
        return BOLE_LIVE;
      case 43:
        return RECOMMEND_TV_NEWS;
      case 44:
        return BOLE_NEWS;
      case 45:
        return SMS_MENU;
      case 46:
        return MEDAL;
      case 47:
        return RECORD;
      case 48:
        return CAROUSEL;
      case 49:
        return RECOMMEND_EPISODE;
      case 50:
        return MEDAL_LIST;
      case 51:
        return PLAYER_CAREER_STAT;
      case 101:
        return BUSINESS;
      case 102:
        return PRODUCT;
      case 103:
        return ACTION;
      case 104:
        return MEMBER_ORDER;
      case 1001:
        return MMS_ALBUM;
      case 1002:
        return MMS_VIDEO;
      case 1003:
        return LETV_LIVE;
      case 1004:
        return TLIVE;
      case 1005:
        return USER_SUBSCRIBE;
      default:
        return null;
    }
  }
}
