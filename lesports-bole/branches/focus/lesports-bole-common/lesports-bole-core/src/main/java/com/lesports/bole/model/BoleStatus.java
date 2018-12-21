package com.lesports.bole.model;

/**
 * Bole状态枚举
 * 
 * @author denghui
 *
 */
public enum BoleStatus {

    /**
     * SMS导入
     */
    IMPORTED(0),
    /**
     * 由于未匹配而新建的
     */
    CREATED(1),
    /**
     * 确认有效的CREATED
     */
    CONFIRMED(2),
    /**
     * 确认无效的CREATED
     */
    INVALID(3),
    /**
     * 已导出到SMS
     */
    EXPORTED(4),
    /**
     * 已关联状态
     */
    ATTACHED(5);

    private int status;

    private BoleStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    /**
     * 判断当前状态是否可以直接更新到目标状态而不进行其他操作
     * 
     * @param target
     * @return
     */
    public boolean isSafeUpdated(BoleStatus target) {
        boolean targetSafe = target == BoleStatus.CREATED || target == BoleStatus.CONFIRMED || target == BoleStatus.INVALID;
        boolean thisSafe = this == BoleStatus.CREATED || this == BoleStatus.CONFIRMED || this == BoleStatus.INVALID;
        return targetSafe && thisSafe;
    }

    /**
     * 根据数值返回状态
     * 
     * @param status
     * @return
     */
    public static BoleStatus fromInt(int status) {
        for (BoleStatus item : BoleStatus.values()) {
            if (item.getStatus() == status) {
                return item;
            }
        }

        throw new IllegalArgumentException("unknown status " + status);
    }

    /**
     * 判断当前状态是否已匹配
     * 
     * @param status
     * @return
     */
    public static boolean isMatched(BoleStatus status) {
        // CREATED状态表示尚未匹配
        return !(BoleStatus.CREATED.equals(status) || BoleStatus.INVALID.equals(status));
    }

    /**
     * 判断当前状态是否可以进行关联操作
     * 
     * @param status
     * @return
     */
    public static boolean canAttach(BoleStatus status) {
        return BoleStatus.CREATED.equals(status) || BoleStatus.ATTACHED.equals(status);
    }
    
    /**
     * 判断当前状态是否可以被关联
     * @param status
     * @return
     */
    public static boolean canBeAttached(BoleStatus status) {
        return BoleStatus.IMPORTED.equals(status) || BoleStatus.EXPORTED.equals(status);
    }
    
    /**
     * 当前状态是否可进行导出操作
     * @param status
     * @return
     */
    public static boolean canExport(BoleStatus status) {
        return BoleStatus.CREATED.equals(status) || BoleStatus.CONFIRMED.equals(status);
    }
}
