package com.grasswort.appium.executable;

import com.grasswort.appium.app.Apps;
import com.grasswort.appium.app.Goal;
import com.grasswort.appium.app.Page;
import com.grasswort.appium.app.wechat.GoalStep;
import com.grasswort.appium.app.wechat.goal.GoalAddFriend;
import com.grasswort.appium.app.wechat.goal.GoalPullFriendToQun;
import com.grasswort.appium.app.wechat.page.FirstPage;
import com.grasswort.appium.driver.DriverProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Random;

public class Wechat implements Run {
    private Logger logger = LoggerFactory.getLogger(Wechat.class);
    // 驱动代理
    private DriverProxy proxy = new DriverProxy(Apps.WECHAT);

    @Override
    public void run() {
        logger.info("那么，开始咯！");
        proxy.forceWait(10);
        // 【1】初始化当前页为首页
        Page currentPage = new FirstPage(proxy);
        boolean random = false;
        // 【2】获取一个任务
        Optional<Goal> goalOpt = this.getAGoal(random);

        while (goalOpt.isPresent()) {
            Goal goal = goalOpt.get();
            currentPage.setGoal(goal);
            while (goal.getStep().equals(GoalStep.UNDERWAY)) {
                currentPage = currentPage.run();
            }
            // 【3】获取下一个任务
            goalOpt = this.getAGoal(random);
        }
        proxy.forceWait(10);
        proxy.quit();
    }


    /**
     * 获取一个Goal
     * @return
     */
    private static int goalIndex = 0;
    private Optional<Goal> getAGoal(boolean isRandom) {
        Goal goal = null;
        if (isRandom) {
            // 随机产生任务
            Random random = new Random();
            goal = generalteAGoal(random.nextInt(4) + 1);

        } else {
            goalIndex ++;
            goal = generalteAGoal(goalIndex);
        }
        if (null != goal) {
            logger.info("接收到新任务：【{}】", goal.goal());
        } else {
            logger.info("没有任务可接收。");
        }
        return Optional.ofNullable(goal);
    }



    /**
     * 随机生成一个Goal
     * @param goalNo
     * @return
     */
    private Goal generalteAGoal(final int goalNo) {
        switch (goalNo) {
            case 1:
                return new GoalAddFriend("13699128031");
            case 2:
                return new GoalAddFriend("18910422873");
            case 3:
                return new GoalAddFriend("18910422875");
            case 4:
                return new GoalPullFriendToQun("测试群");
            default:
                return null;
        }
    }
}
