package com.lesports.crawler.component.priority;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 15-11-13
 */
public enum Priority {
    HIGH(9), LOW(0), DEFAULT(4);
    private int value;
     Priority(int value) {
    this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
