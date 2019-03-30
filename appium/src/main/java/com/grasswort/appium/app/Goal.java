package com.grasswort.appium.app;

import com.grasswort.appium.app.wechat.GoalStep;

/**
 * 目标
 */
public abstract class Goal {

    protected GoalStep step = GoalStep.UNDERWAY;

    public abstract String goal();

    public GoalStep getStep() {
        return step;
    }

    public void setStep(GoalStep step) {
        this.step = step;
    }
}
