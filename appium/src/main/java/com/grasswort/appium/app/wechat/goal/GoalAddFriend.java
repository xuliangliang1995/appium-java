package com.grasswort.appium.app.wechat.goal;

import com.grasswort.appium.app.Goal;

/**
 * 添加好友
 */
public class GoalAddFriend extends Goal {
    private String phone;

    public GoalAddFriend(String phone) {
        this.phone = phone;
    }

    @Override
    public String goal() {
        return String.format("添加好友:【%s】", phone);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
