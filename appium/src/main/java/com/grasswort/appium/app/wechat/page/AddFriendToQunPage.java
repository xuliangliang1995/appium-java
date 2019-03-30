package com.grasswort.appium.app.wechat.page;

import com.grasswort.appium.app.Goal;
import com.grasswort.appium.app.Page;
import com.grasswort.appium.app.wechat.GoalStep;
import com.grasswort.appium.app.wechat.goal.GoalAddFriend;
import com.grasswort.appium.app.wechat.goal.GoalPullFriendToQun;
import com.grasswort.appium.driver.DriverProxy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidElement;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AddFriendToQunPage implements Page {
    private Logger logger = LoggerFactory.getLogger(AddFriendToQunPage.class);
    private DriverProxy driver;
    private Goal goal;

    public AddFriendToQunPage(DriverProxy driver) {
        this.driver = driver;
    }

    private final By USER_BOX = By.id("com.tencent.mm:id/dat");
    private final By NAME_BOX = By.id("com.tencent.mm:id/q0");
    private final By SELECT_BOX = By.id("com.tencent.mm:id/a08");
    private final By CONFIRM_BOX = By.id("com.tencent.mm:id/jx");
    @Override
    public boolean isCurrentPage() {
        boolean bo = driver.exists(USER_BOX);
        if (bo) {
            logger.info("当前已进入添加好友入群页");
        }
        return bo;
    }

    @Override
    public Page run() {
        boolean isAddFriendGoal = goal != null && goal instanceof GoalAddFriend;
        if (isAddFriendGoal) {
            return this.backToFirstPage();
        }

        boolean isPullFriendToQunGoal = goal != null && goal instanceof GoalPullFriendToQun;
        if (isPullFriendToQunGoal) {
            return this.pullFriendToQun();
        }

        return this;
    }

    /**
     * 返回首页
     * @return
     */
    private Page backToFirstPage() {
        driver.getInnnerDriver().get().tap(1, 70, 140, 300);
        driver.forceWait(1);
        driver.getInnnerDriver().get().tap(1, 70, 140, 300);
        driver.forceWait(1);
        driver.getInnnerDriver().get().tap(1, 70, 140, 300);
        driver.forceWait(1);
        FirstPage page2 = new FirstPage(driver);
        page2.setGoal(goal);
        return page2.isCurrentPage() ? page2 : this;
    }

    /**
     * 拉好友到群
     * @return
     */
    private Page pullFriendToQun() {
        final Set<String> selectSet = new HashSet<>(); // 存储已经选中的用户，避免再次点击取消选中状态
        String lastUserName = "";
        while (true) {
            try {
                List<AndroidElement> elementList = driver.findAll(USER_BOX).stream().filter(e ->
                        e.findElements(NAME_BOX).size() > 0
                ).collect(Collectors.toList());
                String currenLastUserName = elementList.isEmpty() ? ""
                        : elementList.get(elementList.size() - 1).findElement(NAME_BOX).getText();
                boolean noMoreUser = Objects.equals(currenLastUserName, lastUserName);
                if (noMoreUser) {
                    break;
                } else {
                    lastUserName = currenLastUserName;
                }
                for (AndroidElement e :elementList) {
                    MobileElement nameBox = e.findElement(NAME_BOX);
                    if (nameBox != null) {
                        String name = nameBox.getText();
                        logger.info("获取微信昵称：{}", name);
                        lastUserName = name;
                        boolean needAdd = ! selectSet.contains(name)
                                && (
                                Objects.equals(name, "C")
                                        || Objects.equals(name, "抠脚的前端程序员")
                        );
                        if (needAdd) {
                            selectSet.add(name);
                            MobileElement fireBtn = e.findElement(SELECT_BOX);
                            if (fireBtn != null) {
                                driver.forceWait(1);
                                driver.getInnnerDriver().get().tap(1, fireBtn.getCenter().x, fireBtn.getCenter().y, 300);
                                driver.forceWait(1);
                            }
                        }
                    }
                }
                driver.swipeToUp();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            driver.forceWait(2);
        }

        AndroidElement confirmBtn =  driver.find(CONFIRM_BOX).get();
        driver.getInnnerDriver().get().tap(1, confirmBtn.getCenter().x, confirmBtn.getCenter().y, 300);
        driver.forceWait(3);
        goal.setStep(GoalStep.ACHIEVE);
        driver.getInnnerDriver().get().tap(1, 70, 150, 300);
        driver.forceWait(1);
        driver.getInnnerDriver().get().tap(1, 70, 150, 300);
        driver.forceWait(1);
        driver.getInnnerDriver().get().tap(1, 70, 150, 300);
        driver.forceWait(1);
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
