package com.grasswort.appium.app.wechat.goal;

import com.grasswort.appium.app.Goal;

public class GoalPullFriendToQun extends Goal {

    private String name;

    public GoalPullFriendToQun(String name) {
        this.name = name;
    }

    @Override
    public String goal() {
        return "拉好友进群" + name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
