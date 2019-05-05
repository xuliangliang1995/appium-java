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

public class UserInfoPage implements Page {
    private Logger logger = LoggerFactory.getLogger(UserInfoPage.class);
    private DriverProxy driver;
    private Goal goal;


    private final By PORTRAIT = By.id("com.tencent.mm:id/b3w");
    private final By ADD_BTN = By.id("com.tencent.mm:id/cs");
    private final By SEND_BTN = By.id("com.tencent.mm:id/jx");
    private final By BACK = By.id("com.tencent.mm:id/kb");

    public UserInfoPage(DriverProxy driver) {
        this.driver = driver;
    }

    @Override
    public boolean isCurrentPage() {
        boolean bo = driver.exists(PORTRAIT);
        if (bo) {
            logger.info("当期页面：用户信息页");
        }
        return bo;
    }

    @Override
    public Page run() {
        boolean isAddFriendGoal = goal != null && goal instanceof GoalAddFriend;
        if (isAddFriendGoal) {
            return this.addFriend();
        }

        boolean isPullFriendToQun = goal != null && goal instanceof GoalPullFriendToQun;
        if (isPullFriendToQun) {
            return this.backToFirstPage();
        }
        return this;
    }

    /**
     * 添加好友（操作）
     */
    private Page addFriend() {
        boolean notAdded = driver.getInnnerDriver().get().getPageSource().indexOf("通讯录") >= 0;
        if (notAdded) {
            driver.getInnnerDriver().get().tap(1, 520, 1000, 300);
            driver.forceWait(5);
            AndroidElement addBtn = driver.find(ADD_BTN).get();
            //driver.getInnnerDriver().get().tap(1, 520, 1200, 300);
            driver.getInnnerDriver().get().tap(1, addBtn.getCenter().x, addBtn.getCenter().y, 300);
            driver.forceWait(5);
            boolean addSuccess = driver.getInnnerDriver().get().getPageSource().indexOf("发消息") >= 0;
            if (addSuccess) {

            } else {
                final By userNote = By.id("com.tencent.mm:id/e0s");
                driver.setValue(userNote, "@" + ((GoalAddFriend)goal).getPhone());
                AndroidElement sendBtn = driver.find(SEND_BTN).get();
                //driver.getInnnerDriver().get().tap(1, 950, 138, 300);
                driver.getInnnerDriver().get().tap(1, sendBtn.getCenter().x, sendBtn.getCenter().y, 300);
                driver.forceWait(5);
            }
        }
        goal.setStep(GoalStep.ACHIEVE);
        driver.getInnnerDriver().get().tap(1, 70, 140, 300);
        driver.forceWait(1);
        driver.getInnnerDriver().get().tap(1, 70, 140, 300);
        driver.forceWait(1);
        driver.getInnnerDriver().get().tap(1, 330, 230, 300);
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
    public Goal getGoal() {
        return goal;
    }

    @Override
    public void setGoal(Goal goal) {
        this.goal = goal;
    }
}
