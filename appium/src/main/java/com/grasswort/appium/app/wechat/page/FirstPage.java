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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 首页
 */
public class FirstPage implements Page {

    private Logger logger = LoggerFactory.getLogger(FirstPage.class);
    private DriverProxy driver;
    private Goal goal;
    private static final By FIRST_PAGE_MARKER = By.id("android:id/text1");
    private final By ADD_BTN_ID = By.id("com.tencent.mm:id/iq");
    private final int ADD_BTN_ORDER_INDEX = 1;
    private final By ADD_FRIEND_BTN_ID = By.id("com.tencent.mm:id/cw");
    private final String ADD_FRIEND_BTN_TEXT = "添加朋友";
    private final By QUN_RESOURCE_ID = By.id("com.tencent.mm:id/b5o");

    @Override
    public Page run() {
        boolean isAddFriendGoal = goal != null && goal instanceof GoalAddFriend;
        if (isAddFriendGoal) {
            // 【目标】添加好友
            return this.addFriend();
        }

        boolean isSelectQunGoal = goal != null && goal instanceof GoalPullFriendToQun;
        if (isSelectQunGoal) {
            // 【目标】拉好友进群
            return this.selectQun();
        }

        return this;
    }

    /**
     * 添加朋友(引导)
     */
    private Page addFriend() {
        List<AndroidElement> eles = driver.findAll(ADD_BTN_ID);
        AndroidElement addBtn = eles.get(ADD_BTN_ORDER_INDEX);
        driver.clickTarget(addBtn, ADD_FRIEND_BTN_ID);
        driver.clickTextFromBy(ADD_FRIEND_BTN_ID, ADD_FRIEND_BTN_TEXT);
        AddFriendBootPage page2 = new AddFriendBootPage(driver);
        page2.setGoal(goal);
        return page2.isCurrentPage() ? page2 : this;
    }

    /**
     * 选择群
     * @return
     */
    private Page selectQun() {
        Optional<AndroidElement> optE = null;
        do {
            if (null != optE) {
                // 上滑
                driver.swipeToUp();
                driver.forceWait(1);
            }
            optE = driver.findAll(QUN_RESOURCE_ID).stream()
                    .filter(e -> Objects.equals(e.getText(), ((GoalPullFriendToQun) goal).getName()))
                    .findFirst();
        } while (! optE.isPresent());
        optE.get().click();
        QunLiaoPage page2 = new QunLiaoPage(driver);
        page2.setGoal(goal);
        return page2.isCurrentPage() ? page2 : this;
    }

    @Override
    public boolean isCurrentPage() {
        List<AndroidElement> elementList = driver.findAll(FIRST_PAGE_MARKER);
        boolean bo = elementList.stream().filter(e -> e.getText().indexOf("微信") >= 0).findFirst().isPresent();
        if (bo) {
            logger.info("当前页面：首页");
        }
        return bo;
    }



    public FirstPage(DriverProxy driver) {
        this.driver = driver;
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
