package com.grasswort.appium.app.wechat.page;

import com.grasswort.appium.app.Goal;
import com.grasswort.appium.app.Page;
import com.grasswort.appium.app.wechat.goal.GoalAddFriend;
import com.grasswort.appium.app.wechat.goal.GoalPullFriendToQun;
import com.grasswort.appium.driver.DriverProxy;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 添加朋友引导页
 */
public class AddFriendBootPage implements Page {
    private Logger logger = LoggerFactory.getLogger(AddFriendBootPage.class);
    private DriverProxy driver;
    private Goal goal;

    private final By ADD_FRIEND_BOOT_BOX = By.id("com.tencent.mm:id/d85");
    private final By BACK = By.id("com.tencent.mm:id/kb");

    public AddFriendBootPage(DriverProxy driver) {
        this.driver = driver;
    }


    @Override
    public Page run() {
        // 添加好友目标
        boolean isAddFriendGoal = goal != null &&goal instanceof GoalAddFriend;
        if (isAddFriendGoal) {
            return this.addFriend();
        }
        // 拉好友进群
        boolean isPullFriendToQun = goal != null && goal instanceof GoalPullFriendToQun;
        if (isPullFriendToQun) {
            return this.backToFirstPage();
        }

        // 返回当前页
        return this;
    }

    /**
     * 添加好友操作
     * @return
     */
    private Page addFriend() {
        // 点击进入搜索页
        driver.click(ADD_FRIEND_BOOT_BOX, false);
        SearchFriendPage page2 = new SearchFriendPage(driver);
        page2.setGoal(goal);
        return page2.isCurrentPage() ? page2 : this;
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
    public boolean isCurrentPage() {
        boolean bo = driver.exists(ADD_FRIEND_BOOT_BOX);
        if (bo) {
            logger.info("当前页面：搜索引导页");
        }
        return bo;
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
