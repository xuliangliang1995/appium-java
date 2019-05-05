package com.grasswort.appium.app.wechat.page;

import com.grasswort.appium.app.Goal;
import com.grasswort.appium.app.Page;
import com.grasswort.appium.app.wechat.GoalStep;
import com.grasswort.appium.app.wechat.goal.GoalAddFriend;
import com.grasswort.appium.app.wechat.goal.GoalPullFriendToQun;
import com.grasswort.appium.driver.DriverProxy;
import io.appium.java_client.android.AndroidElement;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchFriendPage implements Page {
    private Logger logger = LoggerFactory.getLogger(SearchFriendPage.class);
    private DriverProxy driver;
    private Goal goal;

    public SearchFriendPage(DriverProxy driver) {
        this.driver = driver;
    }

    private final By SEARCH_FRIEND_PAGE_MARKER = By.id("com.tencent.mm:id/kh");
    private final By FIND_USER_BTN = By.id("com.tencent.mm:id/r4");
    private final By BACK = By.id("com.tencent.mm:id/kb");
    private final By BACK2 = By.id("com.tencent.mm:id/kf");
    private final By SEARCH_BOOT_BTN = By.id("com.tencent.mm:id/d85");

    @Override
    public boolean isCurrentPage() {
        boolean bo = driver.exists(SEARCH_FRIEND_PAGE_MARKER);
        if (bo) {
            logger.info("当前页面:搜索页");
        }
        return bo;
    }

    @Override
    public Page run() {
        boolean isAddFriendGoal = goal != null && goal instanceof GoalAddFriend;
        if (isAddFriendGoal) {
            return this.addFriend();
        }

        boolean isPullFriendToQunGoal = goal != null && goal instanceof GoalPullFriendToQun;
        if (isPullFriendToQunGoal) {
            return this.backToFirstPage();
        }
        return this;
    }

    /**
     * 添加好友（搜索）
     * @return
     */
    private Page addFriend() {
        driver.findAll(SEARCH_FRIEND_PAGE_MARKER).stream().forEach(e -> e.click());
        driver.setValue(SEARCH_FRIEND_PAGE_MARKER,  ((GoalAddFriend) goal).getPhone());
        driver.waitTargets(FIND_USER_BTN);
        driver.click(FIND_USER_BTN, true);
        driver.forceWait(3);
        UserInfoPage page2 = new UserInfoPage(driver);
        page2.setGoal(goal);
        if (page2.isCurrentPage()) {
            return page2;
        }
        goal.setStep(GoalStep.ACHIEVE);
        AndroidElement backBtn = driver.find(BACK2).get();
        driver.getInnnerDriver().get().tap(1, backBtn.getCenter().x, backBtn.getCenter().y, 300);
        driver.forceWait(1);

        AndroidElement bootBtn = driver.find(SEARCH_BOOT_BTN).get();
        driver.getInnnerDriver().get().tap(1, bootBtn.getCenter().x, bootBtn.getCenter().y, 300);
        driver.forceWait(1);
        return this;
    }

    /**
     * 返回首页
     * @return
     */
    private Page backToFirstPage() {
        while (driver.exists(BACK)) {
            driver.click(BACK, false);
        }
        FirstPage page2 = new FirstPage(driver);
        page2.setGoal(goal);
        return page2.isCurrentPage() ? page2 : this;
    }

    @Override
    public Goal getGoal() {
        return goal;
    }

    @Override
    public void setGoal(Goal goal) {
        this.goal = goal;
    }
}
