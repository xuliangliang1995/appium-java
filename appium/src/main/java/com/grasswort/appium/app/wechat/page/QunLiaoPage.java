package com.grasswort.appium.app.wechat.page;

import com.grasswort.appium.app.Goal;
import com.grasswort.appium.app.Page;
import com.grasswort.appium.app.wechat.goal.GoalAddFriend;
import com.grasswort.appium.app.wechat.goal.GoalPullFriendToQun;
import com.grasswort.appium.driver.DriverProxy;
import io.appium.java_client.android.AndroidElement;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QunLiaoPage implements Page {
    private Logger logger = LoggerFactory.getLogger(QunLiaoPage.class);
    private DriverProxy driver;
    private Goal goal;

    private final By HANDLE = By.id("com.tencent.mm:id/jy");
    private final By ADD_BTN = By.name("添加成员");
    private final By BACK = By.id("com.tencent.mm:id/kb");

    public QunLiaoPage(DriverProxy driver) {
        this.driver = driver;
    }


    @Override
    public boolean isCurrentPage() {
        boolean bo = driver.exists(HANDLE);
        if (bo) {
            logger.info("当前已进入群聊页");
        }
        return bo;
    }

    @Override
    public Page run() {
        boolean isAddFriendGoal = goal != null && goal instanceof GoalAddFriend;
        if (isAddFriendGoal) {
            return this.addFriend();
        }

        boolean isSelectQunGoal = goal != null && goal instanceof GoalPullFriendToQun;
        if (isSelectQunGoal) {
            return pullFriendToQun();
        }
        return this;
    }

    /**
     * 添加好友（引导）
     * @return
     */
    private Page addFriend() {
        // 多点几下返回不会出错，为了防止手机卡，多点几下保障回到首页
        while (driver.exists(BACK)) {
            driver.click(BACK, false);
        }
        FirstPage page2 = new FirstPage(driver);
        page2.setGoal(goal);
        return page2.isCurrentPage() ? page2 : this;
    }

    /**
     * 拉好友进群（引导）
     * @return
     */
    private Page pullFriendToQun() {
        AndroidElement handleE = driver.find(HANDLE).get();
        driver.getInnnerDriver().get().tap(1, handleE.getCenter().x, handleE.getCenter().y, 300);
        driver.forceWait(1);
        driver.swipToFind(ADD_BTN, null);
        AndroidElement addBenE = driver.find(ADD_BTN).get();
        driver.getInnnerDriver().get().tap(1, addBenE.getCenter().x, addBenE.getCenter().y, 300);
        driver.forceWait(1);
        AddFriendToQunPage page2 = new AddFriendToQunPage(driver);
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
