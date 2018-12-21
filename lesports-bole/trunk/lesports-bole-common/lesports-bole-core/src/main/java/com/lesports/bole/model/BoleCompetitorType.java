package com.lesports.bole.model;

/**
 * 对阵类型
 * 
 * @author denghui
 *
 */
public enum BoleCompetitorType {
  /**
   * 球队
   */
  TEAM,
  /**
   * 球员
   */
  PLAYER;
  /**
   * 根据数值返回状态
   *
   * @param type
   * @return
   */
  public static BoleCompetitorType fromInt(int type) {
    for (BoleCompetitorType item : BoleCompetitorType.values()) {
      if (item.ordinal() == type) {
        return item;
      }
    }

    throw new IllegalArgumentException("unknown competitor type " + type);
  }
}
