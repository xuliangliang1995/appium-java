package com.grasswort.appium.app;

/**
 * 页面（页面指定了目标之后便可行动）
 */
public interface Page {

    boolean isCurrentPage();

    Page run();

    Goal getGoal();

    void setGoal(Goal goal);

}
