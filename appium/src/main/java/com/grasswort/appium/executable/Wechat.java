package com.grasswort.appium.executable;

import com.alibaba.fastjson.JSONObject;
import com.grasswort.appium.app.Apps;
import com.grasswort.appium.app.Goal;
import com.grasswort.appium.app.Page;
import com.grasswort.appium.app.wechat.GoalStep;
import com.grasswort.appium.app.wechat.goal.GoalAddFriend;
import com.grasswort.appium.app.wechat.goal.GoalPullFriendToQun;
import com.grasswort.appium.app.wechat.page.FirstPage;
import com.grasswort.appium.driver.DriverProxy;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Wechat implements Run {
    private Logger logger = LoggerFactory.getLogger(Wechat.class);
    // 驱动代理
    private DriverProxy proxy = new DriverProxy(Apps.WECHAT_NOX);
    //http://182.92.105.107:4009
    //http://123.207.163.197:4009
    private final String POST_PHONE_URL = "http://182.92.105.107:4009/wechat/pull/next/%s";
    private static OkHttpClient client =new OkHttpClient.Builder()
            .readTimeout(20, TimeUnit.SECONDS)// 设置读取超时时间
            .writeTimeout(10,TimeUnit.SECONDS)// 设置写的超时时间
            .connectTimeout(10,TimeUnit.SECONDS)// 设置连接超时时间
            .build();

    @Override
    public void run() {
        logger.info("那么，开始咯！");
        proxy.forceWait(30);
        // 【1】初始化当前页为首页
        Page currentPage = new FirstPage(proxy);
        // 【2】获取一个任务
        Optional<Goal> goalOpt = this.getAGoal();

        while (goalOpt.isPresent()) {
            Goal goal = goalOpt.get();
            currentPage.setGoal(goal);
            while (goal.getStep().equals(GoalStep.UNDERWAY)) {
                currentPage = currentPage.run();
            }
            // 【3】获取下一个任务
            goalOpt = this.getAGoal();
        }
        proxy.forceWait(10);
        proxy.quit();
    }


    /**
     * 获取一个Goal
     * @return
     */
    private static int goalIndex = 0;
    private Optional<Goal> getAGoal() {
        Goal goal = generalteAGoal();
        if (null != goal) {
            logger.info("接收到新任务：【{}】", goal.goal());
        } else {
            logger.info("没有任务可接收。");
        }
        return Optional.ofNullable(goal);
    }



    /**
     * 随机生成一个Goal
     * @return
     */
    private static int i = 0;
    private Goal generalteAGoal() {
        i ++;
        if (i == 1) {
            return new GoalAddFriend("18910422873");
        }
        if (i == 500) {
            i = 2;
            return new GoalPullFriendToQun("小劳招聘群运营");
        }
        return this.getPhone().map(phone -> new GoalAddFriend(phone)).orElse(null);
    }

    /**
     * 获取手机号
     * @return
     */
    private Optional<String> getPhone() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        FormBody body = new FormBody.Builder().build();
        Request request = new Request.Builder()
                .url(String.format(POST_PHONE_URL,
                        DigestUtils.md5DigestAsHex("xuliangliang".concat(sdf.format(now)).getBytes())))
                .post(body)
                .build();
        System.out.println(String.format(POST_PHONE_URL,
                DigestUtils.md5DigestAsHex("xuliangliang".concat(sdf.format(now)).getBytes())));
        try {
            Response response = client.newCall(request).execute();
            JSONObject result = JSONObject.parseObject(response.body().string());
            String phone = result.getString("data");
            if (StringUtils.isNotBlank(phone) && ! "null".equals(phone)) {
                return Optional.of(phone);
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
